package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.common.exception.BusinessRuleException;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.jobseeker.config.CvConfigProperties;
import tz.co.halotel.halokazi.jobseeker.dto.cv.*;
import tz.co.halotel.halokazi.jobseeker.mapper.CvMapper;
import tz.co.halotel.halokazi.jobseeker.mapper.CvPrefillMapper;
import tz.co.halotel.halokazi.jobseeker.model.*;
import tz.co.halotel.halokazi.jobseeker.repository.CvDataStore;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/** CV Builder and My CVs use cases over the project's current in-memory stores. */
@Service
public class CvService {

    private static final String DEFAULT_TEMPLATE = "CLASSIC";

    private final CvDataStore cvDataStore;
    private final JobSeekerDataStore profileStore;
    private final CurrentJobSeekerProvider currentJobSeekerProvider;
    private final CvMapper mapper;
    private final CvPrefillMapper prefillMapper;
    private final CvConfigProperties properties;

    public CvService(CvDataStore cvDataStore,
                     JobSeekerDataStore profileStore,
                     CurrentJobSeekerProvider currentJobSeekerProvider,
                     CvMapper mapper,
                     CvPrefillMapper prefillMapper,
                     CvConfigProperties properties) {
        this.cvDataStore = cvDataStore;
        this.profileStore = profileStore;
        this.currentJobSeekerProvider = currentJobSeekerProvider;
        this.mapper = mapper;
        this.prefillMapper = prefillMapper;
        this.properties = properties;
    }

    public List<CvTemplateDto> getTemplates() {
        return cvDataStore.getActiveTemplates().stream().map(mapper::toTemplateDto).toList();
    }

    public synchronized CvDetailDto createDraft(CreateCvDraftRequest request) {
        Long profileId = currentJobSeekerProvider.getCurrentProfileId();
        JobSeekerProfile profile = profileStore.getProfile(profileId);
        if (profile == null) throw new ResourceNotFoundException("Jobseeker profile not found");

        String templateCode = request == null || request.getTemplateCode() == null
                ? DEFAULT_TEMPLATE : request.getTemplateCode();
        CvTemplate template = requireActiveTemplate(templateCode);
        String title = request == null ? null : request.getTitle();
        if (title != null) {
            title = title.trim();
            if (title.isEmpty()) throw new IllegalArgumentException("CV title is required");
        }
        Cv cv = new Cv();
        cv.setProfileId(profileId);
        cv.setTemplateId(template.getTemplateId());
        cv.setTemplateCode(template.getCode());
        cv.setTemplateName(template.getNameEn());
        cv.setCvName(title == null ? nextTitle(profileId) : title);
        cv.setHeadline(profile.getHeadline());
        cv.setSummaryOverride(profile.getProfessionalSummary());
        cv.setStatus(CvStatus.DRAFT);
        cv.setLastStep(CvLastStep.PERSONAL_INFO);
        cv.setPersonalInfo(prefillMapper.fromProfile(profile));
        cv.setExperiences(prefillMapper.fromWorkExperiences(profileStore.getWorkExperiences(profileId)));
        cv.setEducations(prefillMapper.fromEducations(profileStore.getEducations(profileId)));
        cv.setSkills(prefillMapper.fromSkills(profileStore.getSkills(profileId)));
        cv.setCertifications(prefillMapper.fromCertifications(profileStore.getCertifications(profileId)));
        cv.setLanguages(prefillMapper.fromLanguages(profileStore.getLanguages(profileId)));
        cv.setProjects(prefillMapper.fromProjects(profileStore.getProjects(profileId)));
        cv.setReferences(prefillMapper.fromReferences(profileStore.getReferences(profileId)));
        cv.setSections(defaultSections());

        Cv saved = cvDataStore.createCv(cv, maxPerProfile());
        return mapper.toDetail(saved);
    }

    public CvPageDto listMyCvs(int page, int requestedSize) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (requestedSize < 1) throw new IllegalArgumentException("Page size must be at least one");
        int size = Math.min(requestedSize, maxPageSize());
        long offset = (long) page * size;
        CvDataStore.CvSummaryPage slice = cvDataStore.getCvSummaryPage(
                currentJobSeekerProvider.getCurrentProfileId(), offset, size);
        List<CvSummaryDto> content = slice.content().stream().map(mapper::toSummary).toList();
        int totalPages = (int) ((slice.totalElements() + size - 1) / size);
        return new CvPageDto(content, page, size, slice.totalElements(), totalPages);
    }

    public CvDetailDto getCv(Long cvId) {
        return mapper.toDetail(requireOwnedCv(cvId));
    }

    public CvDetailDto savePersonalInfo(Long cvId, int expectedVersion, CvPersonalInfoRequest request) {
        Cv cv = requireOwnedCv(cvId);
        requireDraft(cv);
        mapper.applyPersonalInfo(cv, request);
        cv.setHeadline(request.getHeadline());
        cv.setSummaryOverride(request.getProfessionalSummary());
        cv.setLastStep(CvLastStep.PERSONAL_INFO);
        return mapper.toDetail(cvDataStore.saveCv(cv, expectedVersion));
    }

    public CvDetailDto saveExperiences(Long cvId, int expectedVersion, List<CvExperienceItemRequest> requests) {
        Cv cv = requireEditableDraft(cvId);
        cv.setExperiences(merge(cv.getExperiences(), mapper.toExperiences(requests), CvExperience::getId,
                CvExperience::setId, this::copyExperience, CvExperience::setSortOrder));
        cv.setLastStep(CvLastStep.EXPERIENCE);
        return save(cv, expectedVersion);
    }

    public CvDetailDto saveEducations(Long cvId, int expectedVersion, List<CvEducationItemRequest> requests) {
        Cv cv = requireEditableDraft(cvId);
        cv.setEducations(merge(cv.getEducations(), mapper.toEducations(requests), CvEducation::getId,
                CvEducation::setId, this::copyEducation, CvEducation::setSortOrder));
        cv.setLastStep(CvLastStep.EDUCATION);
        return save(cv, expectedVersion);
    }

    public CvDetailDto saveSkills(Long cvId, int expectedVersion, List<CvSkillItemRequest> requests) {
        Cv cv = requireEditableDraft(cvId);
        cv.setSkills(merge(cv.getSkills(), mapper.toSkills(requests), CvSkill::getId,
                CvSkill::setId, this::copySkill, CvSkill::setSortOrder));
        cv.setLastStep(CvLastStep.SKILLS);
        return save(cv, expectedVersion);
    }

    public CvDetailDto saveCertifications(Long cvId, int expectedVersion, List<CvCertificationItemRequest> requests) {
        Cv cv = requireEditableDraft(cvId);
        cv.setCertifications(merge(cv.getCertifications(), mapper.toCertifications(requests), CvCertification::getId,
                CvCertification::setId, this::copyCertification, CvCertification::setSortOrder));
        cv.setLastStep(CvLastStep.CERTIFICATIONS);
        return save(cv, expectedVersion);
    }

    public CvDetailDto saveLanguages(Long cvId, int expectedVersion, List<CvLanguageItemRequest> requests) {
        Cv cv = requireEditableDraft(cvId);
        cv.setLanguages(merge(cv.getLanguages(), mapper.toLanguageEntries(requests), CvLanguageEntry::getId,
                CvLanguageEntry::setId, this::copyLanguage, CvLanguageEntry::setSortOrder));
        cv.setLastStep(CvLastStep.LANGUAGES);
        return save(cv, expectedVersion);
    }

    public CvDetailDto saveProjects(Long cvId, int expectedVersion, List<CvProjectItemRequest> requests) {
        Cv cv = requireEditableDraft(cvId);
        cv.setProjects(merge(cv.getProjects(), mapper.toProjects(requests), CvProject::getId,
                CvProject::setId, this::copyProject, CvProject::setSortOrder));
        cv.setLastStep(CvLastStep.PROJECTS);
        return save(cv, expectedVersion);
    }

    public CvDetailDto saveReferences(Long cvId, int expectedVersion, List<CvReferenceItemRequest> requests) {
        Cv cv = requireEditableDraft(cvId);
        cv.setReferences(merge(cv.getReferences(), mapper.toReferences(requests), CvReference::getId,
                CvReference::setId, this::copyReference, CvReference::setSortOrder));
        cv.setLastStep(CvLastStep.REFERENCES);
        return save(cv, expectedVersion);
    }

    public CvDetailDto saveSectionConfiguration(Long cvId, int expectedVersion, List<CvSectionUpdateRequest> requests) {
        Cv cv = requireEditableDraft(cvId);
        if (requests == null) throw new IllegalArgumentException("Section configuration is required");
        Map<String, CvSection> existing = (cv.getSections() == null ? List.<CvSection>of() : cv.getSections()).stream()
                .filter(Objects::nonNull)
                .filter(section -> section.getSectionCode() != null)
                .collect(Collectors.toMap(CvSection::getSectionCode, s -> s, (first, ignored) -> first, LinkedHashMap::new));
        Set<String> seen = new HashSet<>();
        for (CvSectionUpdateRequest request : requests) {
            if (request == null) throw new IllegalArgumentException("Section configuration items cannot be null");
            CvSectionCode sectionCode;
            try {
                sectionCode = CvSectionCode.valueOf(request.getSectionCode());
            } catch (IllegalArgumentException | NullPointerException ex) {
                throw new IllegalArgumentException("Unknown CV section code: " + request.getSectionCode());
            }
            String code = sectionCode.name();
            if (!seen.add(code)) throw new IllegalArgumentException("Section code must not be repeated: " + code);
            CvSection section = existing.get(code);
            if (section == null) {
                section = new CvSection(cvId, code, true, existing.size() + 1, null);
                existing.put(code, section);
            }
            if (request.getVisible() != null) section.setVisible(request.getVisible());
            if (request.getDisplayOrder() != null) section.setDisplayOrder(request.getDisplayOrder());
            section.setCustomTitle(request.getCustomTitle());
        }
        cv.setSections(existing.values().stream().sorted(Comparator.comparingInt(CvSection::getDisplayOrder)).toList());
        return save(cv, expectedVersion);
    }

    public CvDetailDto chooseTemplate(Long cvId, int expectedVersion, String templateCode) {
        Cv cv = requireOwnedCv(cvId);
        boolean draft = cv.getStatus() == CvStatus.DRAFT;
        assignTemplate(cv, templateCode);
        if (draft) cv.setLastStep(CvLastStep.TEMPLATE);
        return save(cv, expectedVersion);
    }

    public CvFinalizeResultDto finalizeCv(Long cvId, int expectedVersion) {
        Cv cv = requireOwnedCv(cvId);
        if (cv.getStatus() == CvStatus.COMPLETED) return CvFinalizeResultDto.success(mapper.toSummary(cv));
        requireDraft(cv);
        List<CvValidationErrorDto> errors = validateForFinalize(cv);
        if (!errors.isEmpty()) return CvFinalizeResultDto.failure(errors);
        cv.setStatus(CvStatus.COMPLETED);
        Cv saved = cvDataStore.saveCv(cv, expectedVersion);
        return CvFinalizeResultDto.success(mapper.toSummary(saved));
    }

    public CvDetailDto rename(Long cvId, int expectedVersion, CvTitleUpdateRequest request) {
        Cv cv = requireOwnedCv(cvId);
        if (request == null || request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("CV title is required");
        }
        cv.setCvName(request.getTitle().trim());
        return save(cv, expectedVersion);
    }

    public CvDetailDto duplicate(Long cvId, int expectedVersion) {
        Cv source = requireOwnedCv(cvId);
        Cv duplicate = source.copy();
        duplicate.setCvId(null);
        duplicate.setCreatedAt(null);
        duplicate.setUpdatedAt(null);
        duplicate.setDeletedAt(null);
        duplicate.setVersion(0);
        duplicate.setStatus(CvStatus.DRAFT);
        duplicate.setDefault(false);
        duplicate.setLastStep(CvLastStep.PERSONAL_INFO);
        duplicate.setCvName(nextCopyTitle(source.getProfileId(), source.getCvName()));
        Cv saved = cvDataStore.duplicateCv(source.getProfileId(), cvId, expectedVersion, duplicate, maxPerProfile());
        return mapper.toDetail(saved);
    }

    public CvDetailDto setDefault(Long cvId, int expectedVersion) {
        requireOwnedCv(cvId);
        return mapper.toDetail(cvDataStore.setDefaultForProfile(currentJobSeekerProvider.getCurrentProfileId(), cvId, expectedVersion));
    }

    public void delete(Long cvId, int expectedVersion) {
        requireOwnedCv(cvId);
        cvDataStore.softDeleteCv(currentJobSeekerProvider.getCurrentProfileId(), cvId, expectedVersion);
    }

    public Cv getForDownload(Long cvId) {
        return requireOwnedCv(cvId);
    }

    private CvDetailDto save(Cv cv, int expectedVersion) {
        return mapper.toDetail(cvDataStore.saveCv(cv, expectedVersion));
    }

    private Cv requireEditableDraft(Long cvId) {
        Cv cv = requireOwnedCv(cvId);
        requireDraft(cv);
        return cv;
    }

    private Cv requireOwnedCv(Long cvId) {
        Cv cv = cvDataStore.getCv(cvId);
        if (cv == null || !Objects.equals(cv.getProfileId(), currentJobSeekerProvider.getCurrentProfileId())) {
            throw new ResourceNotFoundException("CV not found");
        }
        return cv;
    }

    private void requireDraft(Cv cv) {
        if (cv.getStatus() != CvStatus.DRAFT) {
            throw BusinessRuleException.unprocessable("CV_NOT_DRAFT", "Only a draft CV can be edited in the builder");
        }
    }

    private CvTemplate requireActiveTemplate(String code) {
        CvTemplate template = cvDataStore.getTemplateByCode(code);
        if (template == null || !"ACTIVE".equals(template.getStatus())) {
            throw new IllegalArgumentException("Unknown or inactive CV template");
        }
        return template;
    }

    private void assignTemplate(Cv cv, String code) {
        CvTemplate template = requireActiveTemplate(code);
        cv.setTemplateId(template.getTemplateId());
        cv.setTemplateCode(template.getCode());
        cv.setTemplateName(template.getNameEn());
    }

    private String nextTitle(Long profileId) {
        String prefix = properties.getDefaultTitlePrefix() == null || properties.getDefaultTitlePrefix().isBlank()
                ? "My CV" : properties.getDefaultTitlePrefix().trim();
        int suffix = cvDataStore.countCvsByProfileId(profileId) + 1;
        String title;
        do {
            String ending = " #" + suffix++;
            title = truncate(prefix, 100 - ending.length()) + ending;
        } while (cvDataStore.titleExistsForProfile(profileId, title, null));
        return title;
    }

    private String nextCopyTitle(Long profileId, String sourceTitle) {
        String base = truncate(sourceTitle, 100 - " (Copy)".length()) + " (Copy)";
        String candidate = base;
        int suffix = 2;
        while (cvDataStore.titleExistsForProfile(profileId, candidate, null)) {
            String ending = " (Copy " + suffix++ + ")";
            candidate = truncate(sourceTitle, 100 - ending.length()) + ending;
        }
        return candidate;
    }

    private String truncate(String value, int maxLength) {
        String safe = value == null ? "CV" : value.trim();
        if (safe.length() <= maxLength) return safe;
        return safe.substring(0, Math.max(1, maxLength)).trim();
    }

    private int maxPerProfile() {
        return Math.max(1, properties.getMaxPerProfile());
    }

    private int maxPageSize() {
        return Math.max(1, properties.getMaxPageSize());
    }

    private List<CvSection> defaultSections() {
        List<CvSection> result = new ArrayList<>();
        int order = 1;
        for (CvSectionCode code : CvSectionCode.values()) {
            result.add(new CvSection(null, code.name(), true, order++, null));
        }
        return result;
    }

    private List<CvValidationErrorDto> validateForFinalize(Cv cv) {
        List<CvValidationErrorDto> errors = new ArrayList<>();
        CvPersonalInfo info = cv.getPersonalInfo();
        addMissing(errors, "PERSONAL_INFO", "firstName", info == null ? null : info.getFirstName(), "First name is required");
        addMissing(errors, "PERSONAL_INFO", "lastName", info == null ? null : info.getLastName(), "Last name is required");
        addMissing(errors, "PERSONAL_INFO", "contactEmail", info == null ? null : info.getContactEmail(), "Contact email is required");
        addMissing(errors, "PERSONAL_INFO", "headline", info == null ? null : info.getHeadline(), "Headline is required");
        if (cv.getTemplateId() == null || cv.getTemplateCode() == null) {
            errors.add(new CvValidationErrorDto("TEMPLATE", "templateCode", "Choose a CV template"));
        }
        if (cv.getExperiences() == null || cv.getExperiences().isEmpty()) {
            errors.add(new CvValidationErrorDto("EXPERIENCE", "experiences", "Add at least one work experience"));
        }
        if (cv.getEducations() == null || cv.getEducations().isEmpty()) {
            errors.add(new CvValidationErrorDto("EDUCATION", "educations", "Add at least one education entry"));
        }
        if (cv.getSkills() == null || cv.getSkills().isEmpty()) {
            errors.add(new CvValidationErrorDto("SKILLS", "skills", "Add at least one skill"));
        }
        return errors;
    }

    private void addMissing(List<CvValidationErrorDto> errors, String section, String field, String value, String message) {
        if (value == null || value.isBlank()) errors.add(new CvValidationErrorDto(section, field, message));
    }

    private <T> List<T> merge(List<T> oldItems, List<T> updates, Function<T, Long> id,
                              BiConsumer<T, Long> setId, BiConsumer<T, T> copier,
                              BiConsumer<T, Integer> setSortOrder) {
        long nextId = (oldItems == null ? List.<T>of() : oldItems).stream()
                .filter(Objects::nonNull)
                .map(id)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1L;
        Map<Long, T> existing = (oldItems == null ? List.<T>of() : oldItems).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(id, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        List<T> merged = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (T update : updates) {
            Long itemId = id.apply(update);
            if (itemId == null) {
                setId.accept(update, nextId++);
                merged.add(update);
                continue;
            }
            if (!seen.add(itemId)) throw new IllegalArgumentException("Section item IDs must not be repeated");
            T current = existing.remove(itemId);
            if (current == null) {
                throw BusinessRuleException.unprocessable("CV_SECTION_ITEM_NOT_FOUND", "Section item does not belong to this CV");
            }
            copier.accept(current, update);
            merged.add(current);
        }
        for (int index = 0; index < merged.size(); index++) {
            setSortOrder.accept(merged.get(index), index + 1);
        }
        return merged;
    }

    private void copyExperience(CvExperience to, CvExperience from) {
        to.setCompanyName(from.getCompanyName()); to.setJobTitle(from.getJobTitle()); to.setEmploymentTypeName(from.getEmploymentTypeName());
        to.setLocation(from.getLocation()); to.setDescription(from.getDescription()); to.setStartDate(from.getStartDate()); to.setEndDate(from.getEndDate());
        to.setCurrent(from.isCurrent()); to.setSortOrder(from.getSortOrder());
    }

    private void copyEducation(CvEducation to, CvEducation from) {
        to.setEducationLevelName(from.getEducationLevelName()); to.setInstitutionName(from.getInstitutionName()); to.setQualification(from.getQualification());
        to.setFieldOfStudy(from.getFieldOfStudy()); to.setGrade(from.getGrade()); to.setStartDate(from.getStartDate()); to.setEndDate(from.getEndDate());
        to.setCurrent(from.isCurrent()); to.setSortOrder(from.getSortOrder());
    }

    private void copySkill(CvSkill to, CvSkill from) {
        to.setSkillName(from.getSkillName()); to.setProficiency(from.getProficiency()); to.setYearsExperience(from.getYearsExperience()); to.setSortOrder(from.getSortOrder());
    }

    private void copyCertification(CvCertification to, CvCertification from) {
        to.setCertificationName(from.getCertificationName()); to.setIssuingOrganization(from.getIssuingOrganization()); to.setCredentialId(from.getCredentialId());
        to.setIssueDate(from.getIssueDate()); to.setExpiryDate(from.getExpiryDate()); to.setCertificateUrl(from.getCertificateUrl()); to.setSortOrder(from.getSortOrder());
    }

    private void copyLanguage(CvLanguageEntry to, CvLanguageEntry from) {
        to.setLanguageName(from.getLanguageName()); to.setLanguageCode(from.getLanguageCode()); to.setProficiency(from.getProficiency()); to.setSortOrder(from.getSortOrder());
    }

    private void copyProject(CvProject to, CvProject from) {
        to.setTitle(from.getTitle()); to.setRole(from.getRole()); to.setDescription(from.getDescription()); to.setProjectUrl(from.getProjectUrl());
        to.setStartDate(from.getStartDate()); to.setEndDate(from.getEndDate()); to.setSortOrder(from.getSortOrder());
    }

    private void copyReference(CvReference to, CvReference from) {
        to.setFullName(from.getFullName()); to.setJobTitle(from.getJobTitle()); to.setOrganization(from.getOrganization());
        to.setRelationship(from.getRelationship()); to.setEmail(from.getEmail()); to.setPhone(from.getPhone()); to.setSortOrder(from.getSortOrder());
    }
}
