package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.dto.ProfileStrengthDto;
import tz.co.halotel.halokazi.jobseeker.dto.ProfileStrengthDto.SectionScoreDto;
import tz.co.halotel.halokazi.jobseeker.model.JobSeekerProfile;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileStrengthService {

    private final JobSeekerDataStore dataStore;

    public ProfileStrengthService(JobSeekerDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public ProfileStrengthDto calculateStrength(Long profileId) {
        JobSeekerProfile profile = dataStore.getProfile(profileId);
        if (profile == null) {
            return new ProfileStrengthDto(0.0, "Profile Not Found");
        }

        List<SectionScoreDto> sections = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        double totalScore = 0.0;

        // 1. CONTACT (10%)
        boolean contactDone = profile.getContactEmail() != null && !profile.getContactEmail().isBlank()
                && profile.getPhone() != null && !profile.getPhone().isBlank();
        sections.add(new SectionScoreDto("CONTACT", "Contact Information", 10.0, contactDone, false));
        if (contactDone) totalScore += 10.0;
        else recommendations.add("Verify your phone number and contact email");

        // 2. EDUCATION (15%)
        boolean eduDone = !dataStore.getEducations(profileId).isEmpty();
        sections.add(new SectionScoreDto("EDUCATION", "Education", 15.0, eduDone, false));
        if (eduDone) totalScore += 15.0;
        else recommendations.add("Add at least one educational qualification");

        // 3. WORK_EXPERIENCE (15%)
        boolean workDone = !dataStore.getWorkExperiences(profileId).isEmpty();
        sections.add(new SectionScoreDto("WORK_EXPERIENCE", "Work Experience", 15.0, workDone, false));
        if (workDone) totalScore += 15.0;
        else recommendations.add("Add your past or current work experience");

        // 4. SKILLS (15%)
        boolean skillsDone = !dataStore.getSkills(profileId).isEmpty();
        sections.add(new SectionScoreDto("SKILLS", "Skills", 15.0, skillsDone, false));
        if (skillsDone) totalScore += 15.0;
        else recommendations.add("Add your key technical or professional skills");

        // 5. CERTIFICATIONS (10%)
        boolean certsDone = !dataStore.getCertifications(profileId).isEmpty();
        sections.add(new SectionScoreDto("CERTIFICATIONS", "Certifications", 10.0, certsDone, false));
        if (certsDone) totalScore += 10.0;
        else recommendations.add("Upload professional certifications or licenses");

        // 6. LANGUAGES (10%)
        boolean langDone = !dataStore.getLanguages(profileId).isEmpty();
        sections.add(new SectionScoreDto("LANGUAGES", "Languages", 10.0, langDone, false));
        if (langDone) totalScore += 10.0;
        else recommendations.add("Specify languages you speak and your proficiency");

        // 7. ADDRESS / LOCATION (10%)
        boolean addressDone = (profile.getRegionId() != null || (profile.getCurrentLocation() != null && !profile.getCurrentLocation().isBlank()));
        sections.add(new SectionScoreDto("ADDRESS", "Address / Location", 10.0, addressDone, false));
        if (addressDone) totalScore += 10.0;
        else recommendations.add("Set your current region and district");

        // 8. PROFESSIONAL SUMMARY (10%)
        boolean summaryDone = profile.getProfessionalSummary() != null && profile.getProfessionalSummary().trim().length() > 20;
        sections.add(new SectionScoreDto("SUMMARY", "Professional Summary", 10.0, summaryDone, false));
        if (summaryDone) totalScore += 10.0;
        else recommendations.add("Write a short professional bio highlighting your achievements");

        // 9. PROFILE PICTURE (5%)
        boolean picDone = profile.getProfilePictureUrl() != null && !profile.getProfilePictureUrl().isBlank();
        sections.add(new SectionScoreDto("PROFILE_PICTURE", "Profile Picture", 5.0, picDone, false));
        if (picDone) totalScore += 5.0;
        else recommendations.add("Upload a professional profile photo");

        // Cap at 100
        double roundedScore = Math.min(100.0, totalScore);

        // Status label
        String statusLabel = "Good";
        if (roundedScore >= 90) statusLabel = "All-Star";
        else if (roundedScore >= 75) statusLabel = "Strong";
        else if (roundedScore >= 50) statusLabel = "Intermediate";
        else statusLabel = "Beginner";

        ProfileStrengthDto result = new ProfileStrengthDto(roundedScore, statusLabel);
        result.setSections(sections);
        result.setMissingRecommendations(recommendations);
        return result;
    }
}
