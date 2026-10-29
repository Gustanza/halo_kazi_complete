package tz.co.halotel.halokazi.lookup;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tz.co.halotel.halokazi.vacancy.JobFilter;
import tz.co.halotel.halokazi.vacancy.JobFilter.DatePosted;
import tz.co.halotel.halokazi.vacancy.JobFilter.ExperienceLevel;
import tz.co.halotel.halokazi.vacancy.JobFilter.SalaryRange;

/** Everything a job filter form needs, in one call. Managed lists come from the database. */
@RestController
@RequestMapping("/api/public/lookups")
class LookupController {

    record Lookups(
            List<LookupRef> regions,
            List<LookupRef> employmentTypes,
            List<LookupRef> categories,
            List<FilterOption> experienceLevels,
            List<FilterOption> salaryRanges,
            List<FilterOption> datePosted) {
    }

    private final JdbcClient jdbc;

    LookupController(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    Lookups all() {
        return new Lookups(
                list("SELECT code, name_en, name_sw FROM region WHERE is_active = 1 ORDER BY name_en"),
                list("SELECT code, name_en, name_sw FROM employment_type WHERE is_active = 1 ORDER BY sort_order, name_en"),
                list("SELECT code, name_en, name_sw FROM job_category WHERE is_active = 1 ORDER BY sort_order, name_en"),
                JobFilter.options(ExperienceLevel.class),
                JobFilter.options(SalaryRange.class),
                JobFilter.options(DatePosted.class));
    }

    private List<LookupRef> list(String sql) {
        return jdbc.sql(sql)
                .query((rs, n) -> new LookupRef(rs.getString("code"), rs.getString("name_en"), rs.getString("name_sw")))
                .list();
    }
}
