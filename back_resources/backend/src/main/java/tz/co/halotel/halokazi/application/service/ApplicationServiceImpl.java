package tz.co.halotel.halokazi.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.halotel.halokazi.application.dto.ApplicationDetailDto;
import tz.co.halotel.halokazi.application.dto.ApplicationStatusHistoryDto;
import tz.co.halotel.halokazi.application.dto.ApplicationSubmitRequest;
import tz.co.halotel.halokazi.application.dto.ApplicationSummaryDto;
import tz.co.halotel.halokazi.application.entity.Application;
import tz.co.halotel.halokazi.application.entity.ApplicationStatusHistory;
import tz.co.halotel.halokazi.application.entity.Cv;
import tz.co.halotel.halokazi.application.entity.StoredFile;
import tz.co.halotel.halokazi.application.repository.ApplicationRepository;
import tz.co.halotel.halokazi.application.repository.ApplicationStatusHistoryRepository;
import tz.co.halotel.halokazi.application.repository.CvRepository;
import tz.co.halotel.halokazi.application.repository.StoredFileRepository;
import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.common.exception.BadRequestException;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;
import tz.co.halotel.halokazi.vacancy.repository.VacancyRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final VacancyRepository vacancyRepository;
    private final CvRepository cvRepository;
    private final StoredFileRepository storedFileRepository;
    private final JdbcTemplate jdbcTemplate;
    private final jakarta.persistence.EntityManager entityManager;

    private static final String[] PALETTE = {
            "#FD5C20", "#1F7A3F", "#F6861F", "#2F8FF5", "#B064F7", "#E3001B", "#0D9488"
    };

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  ApplicationStatusHistoryRepository historyRepository,
                                  VacancyRepository vacancyRepository,
                                  CvRepository cvRepository,
                                  StoredFileRepository storedFileRepository,
                                  JdbcTemplate jdbcTemplate,
                                  jakarta.persistence.EntityManager entityManager) {
        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
        this.vacancyRepository = vacancyRepository;
        this.cvRepository = cvRepository;
        this.storedFileRepository = storedFileRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationSummaryDto> getMyApplications(Long profileId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50));
        Page<Application> appPage = applicationRepository.findByProfileIdWithVacancy(profileId, pageable);

        List<ApplicationSummaryDto> dtos = appPage.getContent().stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                dtos,
                appPage.getNumber(),
                appPage.getSize(),
                appPage.getTotalElements(),
                appPage.getTotalPages(),
                appPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDetailDto getApplicationById(Long applicationId, Long profileId) {
        Application app = applicationRepository.findByApplicationIdAndProfileId(applicationId, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        ApplicationDetailDto dto = new ApplicationDetailDto();
        ApplicationSummaryDto summary = mapToSummaryDto(app);

        dto.setApplicationId(summary.getApplicationId());
        dto.setApplicationRef(summary.getApplicationRef());
        dto.setVacancyId(summary.getVacancyId());
        dto.setJobTitle(summary.getJobTitle());
        dto.setCompanyName(summary.getCompanyName());
        dto.setCompanyInitials(summary.getCompanyInitials());
        dto.setBrandColor(summary.getBrandColor());
        dto.setLocation(summary.getLocation());
        dto.setEmploymentType(summary.getEmploymentType());
        dto.setStatus(summary.getStatus());
        dto.setSubmittedAt(summary.getSubmittedAt());
        dto.setLastEditedAt(summary.getLastEditedAt());

        dto.setCoverLetter(app.getCoverLetter());
        dto.setRequirementsConfirmed(app.getRequirementsConfirmed() != null && app.getRequirementsConfirmed() == 1);
        dto.setProfileSnapshot(app.getProfileSnapshot());
        dto.setCvSnapshot(app.getCvSnapshot());
        dto.setProfileCompletionScore(app.getProfileCompletionAtSubmit());
        dto.setStatusRemarks(app.getStatusRemarks());

        List<ApplicationStatusHistory> historyList = historyRepository.findByApplicationIdOrderByChangedAtAsc(applicationId);
        List<ApplicationStatusHistoryDto> historyDtos = historyList.stream()
                .map(h -> new ApplicationStatusHistoryDto(h.getFromStatus(), h.getToStatus(), h.getChangedAt(), h.getRemarks()))
                .collect(Collectors.toList());
        dto.setStatusHistory(historyDtos);

        return dto;
    }

    @Override
    public ApplicationSummaryDto submitApplication(Long profileId, Long userId, ApplicationSubmitRequest request) {
        // Step 1: Validate vacancy
        Vacancy vacancy = vacancyRepository.findByVacancyIdAndDeletedAtIsNull(request.getVacancyId())
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy", "id", request.getVacancyId()));

        if (!"ACTIVE".equalsIgnoreCase(vacancy.getStatus())) {
            throw new BadRequestException("This vacancy is not open for applications (Status: " + vacancy.getStatus() + ")");
        }
        if (vacancy.getApplicationDeadline().isBefore(OffsetDateTime.now())) {
            throw new BadRequestException("The application deadline for this vacancy has expired.");
        }

        // Step 2: Prevent duplicate active applications
        if (applicationRepository.existsByProfileIdAndVacancyVacancyIdAndStatusNot(profileId, request.getVacancyId(), "REJECTED")) {
            throw new BadRequestException("You have already submitted an active application for this vacancy.");
        }

        if (Boolean.FALSE.equals(request.getRequirementsConfirmed())) {
            throw new BadRequestException("You must confirm that you meet the job requirements to submit your application.");
        }

        // Step 3: Resolve CV and Stored File
        Long cvId = resolveCvId(profileId, request.getCvId());
        Long cvFileId = resolveOrCreateStoredFile(userId);

        // Step 4: Create snapshots JSON
        String profileSnapshotJson = String.format("{\"profileId\": %d, \"snapshotAt\": \"%s\"}", profileId, OffsetDateTime.now());
        String cvSnapshotJson = String.format("{\"cvId\": %d, \"profileId\": %d, \"snapshotAt\": \"%s\"}", cvId, profileId, OffsetDateTime.now());

        // Step 5: Build and save Application
        Application application = new Application();
        application.setVacancy(vacancy);
        application.setProfileId(profileId);
        application.setCvId(cvId);
        application.setCoverLetter(request.getCoverLetter());
        application.setRequirementsConfirmed(1);
        application.setRequirementsConfirmedAt(OffsetDateTime.now());
        application.setProfileSnapshot(profileSnapshotJson);
        application.setCvSnapshot(cvSnapshotJson);
        application.setCvSnapshotFileId(cvFileId);
        application.setStatus("SUBMITTED");
        application.setStatusChangedBy(userId);
        application.setStatusChangedAt(OffsetDateTime.now());
        application.setSubmittedAt(OffsetDateTime.now());
        application.setCreatedAt(OffsetDateTime.now());
        application.setUpdatedAt(OffsetDateTime.now());

        Application saved = applicationRepository.saveAndFlush(application);
        if (entityManager != null) {
            entityManager.refresh(saved);
        }

        return mapToSummaryDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkHasApplied(Long profileId, Long vacancyId) {
        return applicationRepository.existsByProfileIdAndVacancyVacancyIdAndStatusNot(profileId, vacancyId, "REJECTED");
    }

    private Long resolveCvId(Long profileId, Long requestedCvId) {
        if (requestedCvId != null) {
            Cv cv = cvRepository.findByCvIdAndProfileIdAndDeletedAtIsNull(requestedCvId, profileId)
                    .orElseThrow(() -> new BadRequestException("The specified CV does not belong to you or has been deleted."));
            return cv.getCvId();
        }

        // Look for any existing active CV for this profile
        List<Cv> cvList = cvRepository.findByProfileIdAndDeletedAtIsNullOrderByCreatedAtDesc(profileId);
        if (!cvList.isEmpty()) {
            return cvList.get(0).getCvId();
        }

        // If no CV exists yet, create an initial default CV record linked to seeded template 1
        Long templateId = 1L;
        List<Long> tplList = jdbcTemplate.query("SELECT template_id FROM cv_template WHERE ROWNUM = 1", (rs, r) -> rs.getLong("template_id"));
        if (!tplList.isEmpty()) {
            templateId = tplList.get(0);
        }

        Cv newCv = cvRepository.save(new Cv(profileId, templateId, "Default Resume"));
        return newCv.getCvId();
    }

    private Long resolveOrCreateStoredFile(Long userId) {
        String key = "applications/cv_snap_" + UUID.randomUUID().toString();
        StoredFile storedFile = new StoredFile(userId, "APPLICATION_CV_PDF", key, "application_cv.pdf", "application/pdf", 1024L);
        StoredFile saved = storedFileRepository.save(storedFile);
        return saved.getFileId();
    }

    private ApplicationSummaryDto mapToSummaryDto(Application a) {
        ApplicationSummaryDto dto = new ApplicationSummaryDto();
        dto.setApplicationId(a.getApplicationId());
        dto.setApplicationRef(a.getApplicationRef());
        dto.setStatus(a.getStatus());
        dto.setSubmittedAt(a.getSubmittedAt());
        dto.setLastEditedAt(a.getLastEditedAt());

        if (a.getVacancy() != null) {
            Vacancy v = a.getVacancy();
            dto.setVacancyId(v.getVacancyId());
            dto.setJobTitle(v.getJobTitle());

            if (v.getEmployer() != null) {
                String comp = v.getEmployer().getBusinessName();
                dto.setCompanyName(comp);
                dto.setCompanyInitials(deriveInitials(comp));
                dto.setBrandColor(deriveBrandColor(comp));
            }

            if (v.getRegion() != null) {
                dto.setLocation(v.getRegion().getNameEn());
            }

            if (v.getEmploymentType() != null) {
                dto.setEmploymentType(v.getEmploymentType().getNameEn());
            }
        }

        return dto;
    }

    private String deriveInitials(String name) {
        if (name == null || name.isBlank()) return "HK";
        String[] words = name.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, Math.min(3, words[0].length())).toUpperCase();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, words.length); i++) {
            if (!words[i].isEmpty()) {
                sb.append(Character.toUpperCase(words[i].charAt(0)));
            }
        }
        return sb.toString();
    }

    private String deriveBrandColor(String name) {
        if (name == null || name.isBlank()) return PALETTE[0];
        int hash = Math.abs(name.hashCode());
        return PALETTE[hash % PALETTE.length];
    }
}
