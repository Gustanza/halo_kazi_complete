package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Component;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

/**
 * Single point of resolution for the current jobseeker's profile ID.
 *
 * Today this returns {@link JobSeekerDataStore#DEFAULT_PROFILE_ID} (hardcoded).
 * When authentication is added, swap the implementation to read from
 * {@code SecurityContextHolder} or {@code @AuthenticationPrincipal}
 * without changing any calling code.
 */
@Component
public class CurrentJobSeekerProvider {

    /**
     * Returns the profile ID of the currently authenticated jobseeker.
     *
     * @return profile ID (currently hardcoded to the default seed profile)
     */
    public Long getCurrentProfileId() {
        return JobSeekerDataStore.DEFAULT_PROFILE_ID;
    }
}
