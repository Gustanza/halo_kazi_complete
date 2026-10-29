package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.ProfileSkill;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class SkillService {

    private final JobSeekerDataStore dataStore;
    private final ProfileStrengthService strengthService;

    public SkillService(JobSeekerDataStore dataStore, ProfileStrengthService strengthService) {
        this.dataStore = dataStore;
        this.strengthService = strengthService;
    }

    public List<ProfileSkill> getSkills(Long profileId) {
        return dataStore.getSkills(profileId);
    }

    public ProfileSkill saveSkill(Long profileId, ProfileSkill skill) {
        ProfileSkill saved = dataStore.saveSkill(profileId, skill);
        updateProfileCompletion(profileId);
        return saved;
    }

    public boolean deleteSkill(Long profileId, Long skillId) {
        boolean removed = dataStore.deleteSkill(profileId, skillId);
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
