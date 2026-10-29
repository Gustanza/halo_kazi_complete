package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.JobAlertPreference;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

@Service
public class JobAlertService {

    private final JobSeekerDataStore dataStore;

    public JobAlertService(JobSeekerDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public JobAlertPreference getAlertPreferences(Long profileId) {
        return dataStore.getAlertPreferences(profileId);
    }

    public JobAlertPreference updateAlertPreferences(Long profileId, JobAlertPreference preferences) {
        dataStore.saveAlertPreferences(profileId, preferences);
        return preferences;
    }

    public JobAlertPreference toggleAlerts(Long profileId) {
        JobAlertPreference pref = dataStore.getAlertPreferences(profileId);
        pref.setEnabled(!pref.isEnabled());
        dataStore.saveAlertPreferences(profileId, pref);
        return pref;
    }
}
