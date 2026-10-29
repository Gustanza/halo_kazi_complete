package tz.co.halotel.halokazi.saved.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.saved.entity.SavedVacancy;
import tz.co.halotel.halokazi.saved.repository.SavedVacancyRepository;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;
import tz.co.halotel.halokazi.vacancy.repository.VacancyRepository;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedVacancyRepository savedVacancyRepository;
    private final VacancyRepository vacancyRepository;

    private static final String[] PALETTE = {
            "#FD5C20", "#1F7A3F", "#F6861F", "#2F8FF5", "#B064F7", "#E3001B", "#0D9488"
    };

    public SavedJobServiceImpl(SavedVacancyRepository savedVacancyRepository,
                               VacancyRepository vacancyRepository) {
        this.savedVacancyRepository = savedVacancyRepository;
        this.vacancyRepository = vacancyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacancySummaryDto> getSavedJobs(Long profileId) {
        List<SavedVacancy> savedList = savedVacancyRepository.findByProfileIdWithVacancy(profileId);
        return savedList.stream()
                .map(s -> mapToSummaryDto(s.getVacancy()))
                .collect(Collectors.toList());
    }

    @Override
    public void saveVacancy(Long profileId, Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findByVacancyIdAndDeletedAtIsNull(vacancyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy", "id", vacancyId));

        if (!savedVacancyRepository.existsByIdProfileIdAndIdVacancyId(profileId, vacancyId)) {
            SavedVacancy savedVacancy = new SavedVacancy(profileId, vacancy);
            savedVacancyRepository.save(savedVacancy);
        }
    }

    @Override
    public void unsaveVacancy(Long profileId, Long vacancyId) {
        if (savedVacancyRepository.existsByIdProfileIdAndIdVacancyId(profileId, vacancyId)) {
            savedVacancyRepository.deleteByIdProfileIdAndIdVacancyId(profileId, vacancyId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSaved(Long profileId, Long vacancyId) {
        return savedVacancyRepository.existsByIdProfileIdAndIdVacancyId(profileId, vacancyId);
    }

    private VacancySummaryDto mapToSummaryDto(Vacancy v) {
        VacancySummaryDto dto = new VacancySummaryDto();
        dto.setId(v.getVacancyId());
        dto.setTitle(v.getJobTitle());

        if (v.getEmployer() != null) {
            String companyName = v.getEmployer().getBusinessName();
            dto.setCompany(companyName);
            dto.setCompanyId(v.getEmployer().getEmployerId());
            dto.setInitials(deriveInitials(companyName));
            dto.setBrandColor(deriveBrandColor(companyName));
        }

        if (v.getCategory() != null) {
            dto.setIndustry(v.getCategory().getNameEn());
            dto.setCategoryId(v.getCategory().getCategoryId());
        }

        if (v.getRegion() != null) {
            dto.setLocation(v.getRegion().getNameEn());
            dto.setRegionId(v.getRegion().getRegionId());
        }

        if (v.getEmploymentType() != null) {
            dto.setType(v.getEmploymentType().getNameEn());
            dto.setEmploymentTypeId(v.getEmploymentType().getEmploymentTypeId());
        }

        dto.setSalaryDisplay(formatSalary(v));
        dto.setPublishedAt(v.getPublishedAt());
        dto.setDeadline(v.getApplicationDeadline());
        dto.setPositions(v.getNumberOfPositions());
        dto.setSaved(true);

        if (v.getPublishedAt() != null) {
            dto.setNew(v.getPublishedAt().isAfter(OffsetDateTime.now().minusDays(7)));
        }

        return dto;
    }

    private String formatSalary(Vacancy v) {
        if (v.getSalaryMin() == null && v.getSalaryMax() == null) {
            return "Competitive";
        }
        DecimalFormat df = new DecimalFormat("#,###");
        String curr = v.getSalaryCurrency() != null ? v.getSalaryCurrency() : "TZS";
        String period = v.getSalaryPeriod() != null ? "/" + v.getSalaryPeriod().toLowerCase() : "";

        if (v.getSalaryMin() != null && v.getSalaryMax() != null) {
            return curr + " " + df.format(v.getSalaryMin()) + " - " + df.format(v.getSalaryMax()) + " " + period;
        } else if (v.getSalaryMin() != null) {
            return "From " + curr + " " + df.format(v.getSalaryMin()) + " " + period;
        } else {
            return "Up to " + curr + " " + df.format(v.getSalaryMax()) + " " + period;
        }
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
