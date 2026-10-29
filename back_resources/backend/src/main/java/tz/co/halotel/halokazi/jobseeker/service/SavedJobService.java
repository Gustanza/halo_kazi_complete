package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.SavedJob;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class SavedJobService {

    private final JobSeekerDataStore dataStore;

    public SavedJobService(JobSeekerDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<SavedJob> getSavedJobs(Long profileId) {
        return dataStore.getSavedJobs(profileId);
    }

    public SavedJob saveJob(Long profileId, SavedJob job) {
        return dataStore.saveJob(profileId, job);
    }

    public boolean removeSavedJob(Long profileId, Long vacancyId) {
        return dataStore.removeSavedJob(profileId, vacancyId);
    }
}
