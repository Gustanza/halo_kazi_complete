package tz.co.halotel.halokazi.jobseeker.mapper;

import org.springframework.stereotype.Component;
import tz.co.halotel.halokazi.jobseeker.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Deep-copies profile data into CV snapshot models.
 * Called once when a new CV draft is created.
 *
 * <p>All copies are value copies — the CV snapshot is completely
 * independent of the profile. Later profile edits will not affect
 * existing CVs (snapshot isolation rule).</p>
 */
@Component
public class CvPrefillMapper {

    /**
     * Copies personal details from the profile into a CV snapshot.
     */
    public CvPersonalInfo fromProfile(JobSeekerProfile profile) {
        if (profile == null) return new CvPersonalInfo();
        CvPersonalInfo pi = new CvPersonalInfo();
        pi.setFirstName(profile.getFirstName());
        pi.setLastName(profile.getLastName());
        pi.setContactEmail(profile.getContactEmail());
        pi.setPhone(profile.getPhone());
        pi.setAlternatePhone(profile.getAlternatePhone());
        pi.setHeadline(profile.getHeadline());
        pi.setProfessionalSummary(profile.getProfessionalSummary());
        pi.setNationality(profile.getNationality());
        pi.setCurrentLocation(profile.getCurrentLocation());
        pi.setRegionName(profile.getRegionName());
        pi.setDistrictName(profile.getDistrictName());
        pi.setAddressLine(profile.getAddressLine());
        pi.setProfilePictureUrl(profile.getProfilePictureUrl());
        pi.setDateOfBirth(profile.getDateOfBirth() != null ? profile.getDateOfBirth().toString() : null);
        pi.setGender(profile.getGender());
        return pi;
    }

    /**
     * Deep-copies work experience entries from profile into CV snapshots.
     * WorkExperience model has no sortOrder; assigned by list index.
     */
    public List<CvExperience> fromWorkExperiences(List<WorkExperience> experiences) {
        if (experiences == null) return new ArrayList<>();
        List<CvExperience> result = new ArrayList<>();
        int order = 1;
        for (WorkExperience we : experiences) {
            if (we == null) continue;
            CvExperience e = new CvExperience();
            e.setId((long) order);
            e.setCompanyName(we.getCompanyName());
            e.setJobTitle(we.getJobTitle());
            e.setEmploymentTypeName(we.getEmploymentTypeName());
            e.setLocation(we.getLocation());
            e.setDescription(we.getDescription());
            e.setStartDate(we.getStartDate());
            e.setEndDate(we.getEndDate());
            e.setCurrent(we.isCurrent());
            e.setSortOrder(order++);
            result.add(e);
        }
        return result;
    }

    /**
     * Deep-copies education entries from profile into CV snapshots.
     * Education model has no sortOrder; assigned by list index.
     */
    public List<CvEducation> fromEducations(List<Education> educations) {
        if (educations == null) return new ArrayList<>();
        List<CvEducation> result = new ArrayList<>();
        int order = 1;
        for (Education ed : educations) {
            if (ed == null) continue;
            CvEducation e = new CvEducation();
            e.setId((long) order);
            e.setEducationLevelName(ed.getEducationLevelName());
            e.setInstitutionName(ed.getInstitutionName());
            e.setQualification(ed.getQualification());
            e.setFieldOfStudy(ed.getFieldOfStudy());
            e.setGrade(ed.getGrade());
            e.setStartDate(ed.getStartDate());
            e.setEndDate(ed.getEndDate());
            e.setCurrent(ed.isCurrent());
            e.setSortOrder(order++);
            result.add(e);
        }
        return result;
    }

    /**
     * Deep-copies skill entries from profile into CV snapshots.
     * ProfileSkill model has no sortOrder; assigned by list index.
     */
    public List<CvSkill> fromSkills(List<ProfileSkill> skills) {
        if (skills == null) return new ArrayList<>();
        List<CvSkill> result = new ArrayList<>();
        int order = 1;
        for (ProfileSkill ps : skills) {
            if (ps == null) continue;
            CvSkill s = new CvSkill();
            s.setId((long) order);
            s.setSkillName(ps.getSkillName());
            s.setProficiency(ps.getProficiency());
            s.setYearsExperience(ps.getYearsExperience());
            s.setSortOrder(order++);
            result.add(s);
        }
        return result;
    }

    /**
     * Deep-copies certification entries from profile into CV snapshots.
     * Certification model has no sortOrder; assigned by list index.
     */
    public List<CvCertification> fromCertifications(List<Certification> certifications) {
        if (certifications == null) return new ArrayList<>();
        List<CvCertification> result = new ArrayList<>();
        int order = 1;
        for (Certification c : certifications) {
            if (c == null) continue;
            CvCertification cert = new CvCertification();
            cert.setId((long) order);
            cert.setCertificationName(c.getCertificationName());
            cert.setIssuingOrganization(c.getIssuingOrganization());
            cert.setCredentialId(c.getCredentialId());
            cert.setIssueDate(c.getIssueDate());
            cert.setExpiryDate(c.getExpiryDate());
            cert.setCertificateUrl(c.getCertificateUrl());
            cert.setSortOrder(order++);
            result.add(cert);
        }
        return result;
    }

    /**
     * Deep-copies language entries from profile into CV snapshots.
     * ProfileLanguage uses isoCode (not languageCode), and has no sortOrder.
     */
    public List<CvLanguageEntry> fromLanguages(List<ProfileLanguage> languages) {
        if (languages == null) return new ArrayList<>();
        List<CvLanguageEntry> result = new ArrayList<>();
        int order = 1;
        for (ProfileLanguage pl : languages) {
            if (pl == null) continue;
            CvLanguageEntry l = new CvLanguageEntry();
            l.setId((long) order);
            l.setLanguageName(pl.getLanguageName());
            l.setLanguageCode(pl.getIsoCode());
            l.setProficiency(pl.getProficiency());
            l.setSortOrder(order++);
            result.add(l);
        }
        return result;
    }

    /**
     * Deep-copies project entries from profile into CV snapshots.
     * ProfileProject has sortOrder.
     */
    public List<CvProject> fromProjects(List<ProfileProject> projects) {
        if (projects == null) return new ArrayList<>();
        List<CvProject> result = new ArrayList<>();
        int order = 1;
        for (ProfileProject pp : projects) {
            if (pp == null) continue;
            CvProject p = new CvProject();
            p.setId((long) order++);
            p.setTitle(pp.getTitle());
            p.setRole(pp.getRole());
            p.setDescription(pp.getDescription());
            p.setProjectUrl(pp.getProjectUrl());
            p.setStartDate(pp.getStartDate());
            p.setEndDate(pp.getEndDate());
            p.setSortOrder(pp.getSortOrder());
            result.add(p);
        }
        return result;
    }

    /**
     * Deep-copies reference entries from profile into CV snapshots.
     * ProfileReference has sortOrder.
     */
    public List<CvReference> fromReferences(List<ProfileReference> references) {
        if (references == null) return new ArrayList<>();
        List<CvReference> result = new ArrayList<>();
        int order = 1;
        for (ProfileReference pr : references) {
            if (pr == null) continue;
            CvReference r = new CvReference();
            r.setId((long) order++);
            r.setFullName(pr.getFullName());
            r.setJobTitle(pr.getJobTitle());
            r.setOrganization(pr.getOrganization());
            r.setRelationship(pr.getRelationship());
            r.setEmail(pr.getEmail());
            r.setPhone(pr.getPhone());
            r.setSortOrder(pr.getSortOrder());
            result.add(r);
        }
        return result;
    }
}
