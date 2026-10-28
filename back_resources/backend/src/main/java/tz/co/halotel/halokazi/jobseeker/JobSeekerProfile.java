package tz.co.halotel.halokazi.jobseeker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** JOB_SEEKER_PROFILE - only what registration needs; other columns keep their database defaults. */
@Entity
@Table(name = "job_seeker_profile")
public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    protected JobSeekerProfile() {
    }

    public static JobSeekerProfile emptyFor(Long userId) {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.userId = userId;
        return profile;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
}
