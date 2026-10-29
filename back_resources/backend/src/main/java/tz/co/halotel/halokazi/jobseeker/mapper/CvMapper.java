package tz.co.halotel.halokazi.jobseeker.mapper;

import org.springframework.stereotype.Component;
import tz.co.halotel.halokazi.jobseeker.dto.cv.*;
import tz.co.halotel.halokazi.jobseeker.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Maps between CV model objects and DTOs.
 * All mapping logic is centralised here — controllers and services never do manual mapping.
 */
@Component
public class CvMapper {

    // ==================== Model -> Summary DTO ====================

    public CvSummaryDto toSummary(Cv cv) {
        if (cv == null) throw new IllegalArgumentException("CV is required");
        CvSummaryDto dto = new CvSummaryDto();
        dto.setCvId(cv.getCvId());
        dto.setCvName(cv.getCvName());
        dto.setTemplateCode(cv.getTemplateCode());
        dto.setTemplateName(cv.getTemplateName());
        dto.setStatus(cv.getStatus() != null ? cv.getStatus().name() : null);
        dto.setDefault(cv.isDefault());
        dto.setCvLanguage(cv.getCvLanguage());
        dto.setUpdatedAt(cv.getUpdatedAt());
        dto.setCreatedAt(cv.getCreatedAt());
        return dto;
    }

    public CvSummaryDto toSummary(CvSummaryProjection cv) {
        if (cv == null) throw new IllegalArgumentException("CV summary is required");
        CvSummaryDto dto = new CvSummaryDto();
        dto.setCvId(cv.cvId());
        dto.setCvName(cv.cvName());
        dto.setTemplateCode(cv.templateCode());
        dto.setTemplateName(cv.templateName());
        dto.setStatus(cv.status() == null ? null : cv.status().name());
        dto.setDefault(cv.isDefault());
        dto.setCvLanguage(cv.cvLanguage());
        dto.setUpdatedAt(cv.updatedAt());
        dto.setCreatedAt(cv.createdAt());
        return dto;
    }

    // ==================== Model -> Detail DTO ====================

    public CvDetailDto toDetail(Cv cv) {
        if (cv == null) throw new IllegalArgumentException("CV is required");
        CvDetailDto dto = new CvDetailDto();
        dto.setCvId(cv.getCvId());
        dto.setCvName(cv.getCvName());
        dto.setTemplateId(cv.getTemplateId());
        dto.setTemplateCode(cv.getTemplateCode());
        dto.setTemplateName(cv.getTemplateName());
        dto.setStatus(cv.getStatus() != null ? cv.getStatus().name() : null);
        dto.setDefault(cv.isDefault());
        dto.setLastStep(cv.getLastStep() != null ? cv.getLastStep().name() : null);
        dto.setCvLanguage(cv.getCvLanguage());
        dto.setHeadline(cv.getHeadline());
        dto.setSummaryOverride(cv.getSummaryOverride());
        dto.setVersion(cv.getVersion());
        dto.setCreatedAt(cv.getCreatedAt());
        dto.setUpdatedAt(cv.getUpdatedAt());

        dto.setPersonalInfo(cv.getPersonalInfo());
        dto.setExperiences(cv.getExperiences() == null ? List.of() : cv.getExperiences());
        dto.setEducations(cv.getEducations() == null ? List.of() : cv.getEducations());
        dto.setSkills(cv.getSkills() == null ? List.of() : cv.getSkills());
        dto.setCertifications(cv.getCertifications() == null ? List.of() : cv.getCertifications());
        dto.setLanguages(cv.getLanguages() == null ? List.of() : cv.getLanguages());
        dto.setProjects(cv.getProjects() == null ? List.of() : cv.getProjects());
        dto.setReferences(cv.getReferences() == null ? List.of() : cv.getReferences());

        dto.setSectionConfig(toSectionConfigList(cv.getSections()));
        return dto;
    }

    // ==================== Template Model -> DTO ====================

    public CvTemplateDto toTemplateDto(CvTemplate template) {
        if (template == null) throw new IllegalArgumentException("CV template is required");
        CvTemplateDto dto = new CvTemplateDto();
        dto.setTemplateId(template.getTemplateId());
        dto.setCode(template.getCode());
        dto.setNameEn(template.getNameEn());
        dto.setNameSw(template.getNameSw());
        dto.setDescriptionEn(template.getDescriptionEn());
        dto.setDescriptionSw(template.getDescriptionSw());
        dto.setSortOrder(template.getSortOrder());
        return dto;
    }

    // ==================== Section Config -> DTO ====================

    private List<CvDetailDto.SectionConfig> toSectionConfigList(List<CvSection> sections) {
        if (sections == null) return List.of();
        return sections.stream()
                .filter(Objects::nonNull)
                .map(s -> {
                    String title = s.getCustomTitle();
                    if (title == null || title.isBlank()) {
                        title = resolveDefaultTitle(s.getSectionCode());
                    }
                    return new CvDetailDto.SectionConfig(
                            s.getSectionCode(), title, s.isVisible(), s.getDisplayOrder());
                })
                .sorted(java.util.Comparator.comparingInt(CvDetailDto.SectionConfig::getDisplayOrder))
                .collect(Collectors.toList());
    }

    private String resolveDefaultTitle(String sectionCode) {
        if (sectionCode == null || sectionCode.isBlank()) return "Section";
        try {
            return CvSectionCode.valueOf(sectionCode).getDefaultTitle();
        } catch (IllegalArgumentException e) {
            return sectionCode;
        }
    }

    // ==================== Request DTOs -> Model (section updates) ====================

    public void applyPersonalInfo(Cv cv, CvPersonalInfoRequest req) {
        if (cv == null || req == null) throw new IllegalArgumentException("CV and personal information are required");
        CvPersonalInfo pi = cv.getPersonalInfo();
        if (pi == null) {
            pi = new CvPersonalInfo();
            cv.setPersonalInfo(pi);
        }
        pi.setFirstName(req.getFirstName());
        pi.setLastName(req.getLastName());
        pi.setContactEmail(req.getContactEmail());
        pi.setPhone(req.getPhone());
        pi.setAlternatePhone(req.getAlternatePhone());
        pi.setHeadline(req.getHeadline());
        pi.setProfessionalSummary(req.getProfessionalSummary());
        pi.setNationality(req.getNationality());
        pi.setCurrentLocation(req.getCurrentLocation());
        pi.setRegionName(req.getRegionName());
        pi.setDistrictName(req.getDistrictName());
        pi.setAddressLine(req.getAddressLine());
        pi.setProfilePictureUrl(req.getProfilePictureUrl());
        pi.setDateOfBirth(req.getDateOfBirth());
        pi.setGender(req.getGender());
    }

    public List<CvExperience> toExperiences(List<CvExperienceItemRequest> items) {
        return mapItems(items, this::toExperience, CvExperience::setSortOrder);
    }

    private CvExperience toExperience(CvExperienceItemRequest req) {
        CvExperience e = new CvExperience();
        e.setId(req.getId());
        e.setCompanyName(req.getCompanyName());
        e.setJobTitle(req.getJobTitle());
        e.setEmploymentTypeName(req.getEmploymentTypeName());
        e.setLocation(req.getLocation());
        e.setDescription(req.getDescription());
        e.setStartDate(req.getStartDate());
        e.setEndDate(req.getEndDate());
        e.setCurrent(req.isCurrent());
        return e;
    }

    public List<CvEducation> toEducations(List<CvEducationItemRequest> items) {
        return mapItems(items, this::toEducation, CvEducation::setSortOrder);
    }

    private CvEducation toEducation(CvEducationItemRequest req) {
        CvEducation e = new CvEducation();
        e.setId(req.getId());
        e.setEducationLevelName(req.getEducationLevelName());
        e.setInstitutionName(req.getInstitutionName());
        e.setQualification(req.getQualification());
        e.setFieldOfStudy(req.getFieldOfStudy());
        e.setGrade(req.getGrade());
        e.setStartDate(req.getStartDate());
        e.setEndDate(req.getEndDate());
        e.setCurrent(req.isCurrent());
        return e;
    }

    public List<CvSkill> toSkills(List<CvSkillItemRequest> items) {
        return mapItems(items, this::toSkill, CvSkill::setSortOrder);
    }

    private CvSkill toSkill(CvSkillItemRequest req) {
        CvSkill s = new CvSkill();
        s.setId(req.getId());
        s.setSkillName(req.getSkillName());
        s.setProficiency(req.getProficiency());
        s.setYearsExperience(req.getYearsExperience());
        return s;
    }

    public List<CvCertification> toCertifications(List<CvCertificationItemRequest> items) {
        return mapItems(items, this::toCertification, CvCertification::setSortOrder);
    }

    private CvCertification toCertification(CvCertificationItemRequest req) {
        CvCertification c = new CvCertification();
        c.setId(req.getId());
        c.setCertificationName(req.getCertificationName());
        c.setIssuingOrganization(req.getIssuingOrganization());
        c.setCredentialId(req.getCredentialId());
        c.setIssueDate(req.getIssueDate());
        c.setExpiryDate(req.getExpiryDate());
        c.setCertificateUrl(req.getCertificateUrl());
        return c;
    }

    public List<CvLanguageEntry> toLanguageEntries(List<CvLanguageItemRequest> items) {
        return mapItems(items, this::toLanguageEntry, CvLanguageEntry::setSortOrder);
    }

    private CvLanguageEntry toLanguageEntry(CvLanguageItemRequest req) {
        CvLanguageEntry l = new CvLanguageEntry();
        l.setId(req.getId());
        l.setLanguageName(req.getLanguageName());
        l.setLanguageCode(req.getLanguageCode());
        l.setProficiency(req.getProficiency());
        return l;
    }

    public List<CvProject> toProjects(List<CvProjectItemRequest> items) {
        return mapItems(items, this::toProject, CvProject::setSortOrder);
    }

    private CvProject toProject(CvProjectItemRequest req) {
        CvProject p = new CvProject();
        p.setId(req.getId());
        p.setTitle(req.getTitle());
        p.setRole(req.getRole());
        p.setDescription(req.getDescription());
        p.setProjectUrl(req.getProjectUrl());
        p.setStartDate(req.getStartDate());
        p.setEndDate(req.getEndDate());
        return p;
    }

    public List<CvReference> toReferences(List<CvReferenceItemRequest> items) {
        return mapItems(items, this::toReference, CvReference::setSortOrder);
    }

    private CvReference toReference(CvReferenceItemRequest req) {
        CvReference r = new CvReference();
        r.setId(req.getId());
        r.setFullName(req.getFullName());
        r.setJobTitle(req.getJobTitle());
        r.setOrganization(req.getOrganization());
        r.setRelationship(req.getRelationship());
        r.setEmail(req.getEmail());
        r.setPhone(req.getPhone());
        return r;
    }

    private <R, T> List<T> mapItems(List<R> requests, Function<R, T> mapper, BiConsumer<T, Integer> setSortOrder) {
        if (requests == null) return List.of();
        List<T> result = new ArrayList<>(requests.size());
        for (int index = 0; index < requests.size(); index++) {
            T item = mapper.apply(requireItem(requests.get(index)));
            setSortOrder.accept(item, index + 1);
            result.add(item);
        }
        return result;
    }

    private <T> T requireItem(T item) {
        if (item == null) throw new IllegalArgumentException("CV section items cannot be null");
        return item;
    }
}
