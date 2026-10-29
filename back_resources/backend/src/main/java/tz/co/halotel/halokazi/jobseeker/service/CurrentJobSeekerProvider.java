package tz.co.halotel.halokazi.jobseeker.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfileRepository;
import tz.co.halotel.halokazi.session.AuthenticatedUser;

/**
 * Single point of resolution for the current jobseeker's profile ID.
 *
 * Reads the caller that {@code SessionAuthInterceptor} (session package) already authenticated
 * from the bearer token and attached to the request - the same identity every real, secure
 * module (applications, job alerts, notifications, saved jobs, account settings) uses. This
 * used to return a hardcoded demo profile id regardless of who was signed in; every caller of
 * this class is unaffected by that fix, per the class's own original contract.
 */
@Component
public class CurrentJobSeekerProvider {

    // Mirrors SessionAuthInterceptor.ATTRIBUTE (package-private in `session`) without a cross-package
    // reference - the key is just AuthenticatedUser's class name, fixed by the servlet attribute contract.
    private static final String AUTHENTICATED_USER_ATTRIBUTE = AuthenticatedUser.class.getName();

    private final HttpServletRequest request;
    private final JobSeekerProfileRepository profiles;

    public CurrentJobSeekerProvider(HttpServletRequest request, JobSeekerProfileRepository profiles) {
        this.request = request;
        this.profiles = profiles;
    }

    /**
     * Returns the profile ID of the currently authenticated jobseeker.
     *
     * @throws ApiException UNAUTHENTICATED (no signed-in caller) or JOB_SEEKERS_ONLY (caller isn't
     *                       a job seeker - e.g. an employer hit a job-seeker-only endpoint)
     */
    public Long getCurrentProfileId() {
        if (!(request.getAttribute(AUTHENTICATED_USER_ATTRIBUTE) instanceof AuthenticatedUser user)) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }
        return profiles.findByUserId(user.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.JOB_SEEKERS_ONLY))
                .getId();
    }
}
