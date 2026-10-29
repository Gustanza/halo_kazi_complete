package tz.co.halotel.halokazi.vacancy;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import tz.co.halotel.halokazi.lookup.LookupRef;

/** Public, read-only shapes of an open vacancy. */
public final class JobViews {

    private JobViews() {
    }

    /** The hiring company as shown on a job card. {@code initials} is badge text until logos exist. */
    public record Company(Long id, String name, String initials) {
    }

    /** Advertised pay. Either bound may be null; the whole object is null when no salary is advertised. */
    public record Salary(BigDecimal min, BigDecimal max, String currency, String period) {
    }

    /** One job card. */
    public record JobSummary(
            Long id,
            String title,
            Company company,
            LookupRef region,
            LookupRef employmentType,
            LookupRef category,
            Salary salary,
            OffsetDateTime publishedAt,
            OffsetDateTime applicationDeadline) {
    }

    /** The full vacancy. */
    public record JobDetail(
            Long id,
            String title,
            Company company,
            LookupRef region,
            String district,
            String locationDetails,
            LookupRef employmentType,
            LookupRef category,
            Salary salary,
            String description,
            String requirements,
            String qualifications,
            String workExperience,
            BigDecimal minExperienceYears,
            LookupRef minEducationLevel,
            int numberOfPositions,
            OffsetDateTime publishedAt,
            OffsetDateTime applicationDeadline) {
    }
}
