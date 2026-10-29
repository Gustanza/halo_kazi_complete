package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.jobseeker.dto.JobApplicationRequest;
import tz.co.halotel.halokazi.jobseeker.model.JobApplication;
import tz.co.halotel.halokazi.jobseeker.model.JobSeekerProfile;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobSeekerDataStore dataStore;

    public JobApplicationService(JobSeekerDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<JobApplication> getApplications(Long profileId) {
        return dataStore.getApplications(profileId);
    }

    public JobApplication getApplication(Long profileId, Long applicationId) {
        return dataStore.getApplications(profileId).stream()
                .filter(a -> a.getApplicationId().equals(applicationId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));
    }

    public JobApplication submitApplication(Long profileId, JobApplicationRequest request) {
        JobSeekerProfile profile = dataStore.getProfile(profileId);

        JobApplication app = new JobApplication();
        app.setVacancyId(request.getVacancyId());
        app.setJobTitle("Digital Specialist Opportunity #" + request.getVacancyId());
        app.setCompanyName("Halokazi Partner Enterprise");
        app.setLocation(profile != null ? profile.getCurrentLocation() : "Dar es Salaam, Tanzania");
        app.setCvId(request.getCvId() != null ? request.getCvId() : 1L);
        app.setCvName("John_Mwangi_Generated_CV.pdf");
        app.setCoverLetter(request.getCoverLetter());
        app.setProfileCompletionAtSubmit(
                profile != null && profile.getCompletionPercentage() != null ? profile.getCompletionPercentage()
                        : 80.0);
        app.setStatusRemarks("Application submitted successfully. Awaiting employer review.");

        return dataStore.saveApplication(profileId, app);
    }
}
