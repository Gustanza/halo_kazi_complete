package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.WorkExperience;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class WorkExperienceService {

    private final JobSeekerDataStore dataStore;
    private final ProfileStrengthService strengthService;

    public WorkExperienceService(JobSeekerDataStore dataStore, ProfileStrengthService strengthService) {
        this.dataStore = dataStore;
        this.strengthService = strengthService;
    }

    public List<WorkExperience> getWorkExperiences(Long profileId) {
        return dataStore.getWorkExperiences(profileId);
    }

    public WorkExperience saveWorkExperience(Long profileId, WorkExperience exp) {
        WorkExperience saved = dataStore.saveWorkExperience(profileId, exp);
        updateProfileCompletion(profileId);
        return saved;
    }

    public boolean deleteWorkExperience(Long profileId, Long workExpId) {
        boolean removed = dataStore.deleteWorkExperience(profileId, workExpId);
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
