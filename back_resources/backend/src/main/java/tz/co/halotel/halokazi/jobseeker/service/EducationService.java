package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.Education;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class EducationService {

    private final JobSeekerDataStore dataStore;
    private final ProfileStrengthService strengthService;

    public EducationService(JobSeekerDataStore dataStore, ProfileStrengthService strengthService) {
        this.dataStore = dataStore;
        this.strengthService = strengthService;
    }

    public List<Education> getEducations(Long profileId) {
        return dataStore.getEducations(profileId);
    }

    public Education saveEducation(Long profileId, Education edu) {
        Education saved = dataStore.saveEducation(profileId, edu);
        updateProfileCompletion(profileId);
        return saved;
    }

    public boolean deleteEducation(Long profileId, Long educationId) {
        boolean removed = dataStore.deleteEducation(profileId, educationId);
        updateProfileCompletion(profileId);
        return removed;
    }

    private void updateProfileCompletion(Long profileId) {
        double score = strengthService.calculateStrength(profileId).getOverallPercentage();
        if (dataStore.getProfile(profileId) != null) {
            dataStore.getProfile(profileId).setCompletionPercentage(score);
        }
    }
}
