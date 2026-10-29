package tz.co.halotel.halokazi.jobseeker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalised configuration for the CV Builder / My CVs feature.
 *
 * Override in application.properties:
 * <pre>
 *   halokazi.cv.max-per-profile=10
 *   halokazi.cv.max-page-size=50
 *   halokazi.cv.default-title-prefix=My CV
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "halokazi.cv")
public class CvConfigProperties {

    /** Maximum number of active (non-deleted) CVs a single jobseeker may own. */
    private int maxPerProfile = 10;

    /** Maximum page size the list endpoint will honour. */
    private int maxPageSize = 50;

    /** Prefix used when auto-generating a CV title (e.g. "My CV #3"). */
    private String defaultTitlePrefix = "My CV";

    public int getMaxPerProfile() {
        return maxPerProfile;
    }

    public void setMaxPerProfile(int maxPerProfile) {
        this.maxPerProfile = maxPerProfile;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public String getDefaultTitlePrefix() {
        return defaultTitlePrefix;
    }

    public void setDefaultTitlePrefix(String defaultTitlePrefix) {
        this.defaultTitlePrefix = defaultTitlePrefix;
    }
}
