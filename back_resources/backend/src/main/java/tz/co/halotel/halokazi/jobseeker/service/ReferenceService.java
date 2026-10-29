package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.ProfileReference;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class ReferenceService {

    private final JobSeekerDataStore dataStore;

    public ReferenceService(JobSeekerDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<ProfileReference> getReferences(Long profileId) {
        return dataStore.getReferences(profileId);
    }

    public ProfileReference saveReference(Long profileId, ProfileReference reference) {
        return dataStore.saveReference(profileId, reference);
    }

    public boolean deleteReference(Long profileId, Long referenceId) {
        return dataStore.deleteReference(profileId, referenceId);
    }
}
