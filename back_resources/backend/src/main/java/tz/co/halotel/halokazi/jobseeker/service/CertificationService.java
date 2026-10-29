package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.Certification;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class CertificationService {

    private final JobSeekerDataStore dataStore;
    private final ProfileStrengthService strengthService;

    public CertificationService(JobSeekerDataStore dataStore, ProfileStrengthService strengthService) {
        this.dataStore = dataStore;
        this.strengthService = strengthService;
    }

    public List<Certification> getCertifications(Long profileId) {
        return dataStore.getCertifications(profileId);
    }

    public Certification saveCertification(Long profileId, Certification cert) {
        Certification saved = dataStore.saveCertification(profileId, cert);
        updateProfileCompletion(profileId);
        return saved;
    }

    public boolean deleteCertification(Long profileId, Long certId) {
        boolean removed = dataStore.deleteCertification(profileId, certId);
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
