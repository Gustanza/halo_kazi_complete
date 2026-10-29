package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.jobseeker.model.ProfileProject;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

@Service
public class ProjectService {

    private final JobSeekerDataStore dataStore;

    public ProjectService(JobSeekerDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<ProfileProject> getProjects(Long profileId) {
        return dataStore.getProjects(profileId);
    }

    public ProfileProject saveProject(Long profileId, ProfileProject project) {
        return dataStore.saveProject(profileId, project);
    }

    public boolean deleteProject(Long profileId, Long projectId) {
        return dataStore.deleteProject(profileId, projectId);
    }
}
