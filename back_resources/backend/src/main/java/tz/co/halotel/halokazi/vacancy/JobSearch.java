package tz.co.halotel.halokazi.vacancy;

import tz.co.halotel.halokazi.vacancy.JobFilter.DatePosted;
import tz.co.halotel.halokazi.vacancy.JobFilter.ExperienceLevel;
import tz.co.halotel.halokazi.vacancy.JobFilter.SalaryRange;

/** Parsed job-search filters; every field is optional (null = not filtered). */
record JobSearch(
        String keyword,
        String regionCode,
        String employmentTypeCode,
        String categoryCode,
        ExperienceLevel experience,
        SalaryRange salary,
        DatePosted posted) {

    static JobSearch of(String q, String region, String type, String category, String experience, String salary,
            String posted) {
        return new JobSearch(blankToNull(q), upper(region), upper(type), upper(category),
                JobFilter.parse(ExperienceLevel.class, "experience", experience),
                JobFilter.parse(SalaryRange.class, "salary", salary),
                JobFilter.parse(DatePosted.class, "posted", posted));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String upper(String value) {
        String v = blankToNull(value);
        return v == null ? null : v.toUpperCase(java.util.Locale.ROOT);
    }
}
