package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.ProfileLanguage;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class LanguageService {

    private final JobSeekerDataStore dataStore;
    private final ProfileStrengthService strengthService;

    public LanguageService(JobSeekerDataStore dataStore, ProfileStrengthService strengthService) {
        this.dataStore = dataStore;
        this.strengthService = strengthService;
    }

    public List<ProfileLanguage> getLanguages(Long profileId) {
        return dataStore.getLanguages(profileId);
    }

    public ProfileLanguage saveLanguage(Long profileId, ProfileLanguage lang) {
        ProfileLanguage saved = dataStore.saveLanguage(profileId, lang);
        updateProfileCompletion(profileId);
        return saved;
    }

    public boolean deleteLanguage(Long profileId, Long langId) {
        boolean removed = dataStore.deleteLanguage(profileId, langId);
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
