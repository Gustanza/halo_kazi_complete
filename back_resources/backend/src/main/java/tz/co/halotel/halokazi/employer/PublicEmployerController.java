package tz.co.halotel.halokazi.employer;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.common.util.Initials;
import tz.co.halotel.halokazi.lookup.LookupRef;

/** Public employer listings. Only approved employers with at least one open job appear. */
@RestController
@RequestMapping("/api/public/employers")
class PublicEmployerController {

    static final int MAX_LIMIT = 20;

    record TopEmployer(Long id, String name, String initials, LookupRef industry, int openJobs) {
    }

    private final JdbcClient jdbc;

    PublicEmployerController(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Employers ranked by how many open jobs they have right now. */
    @GetMapping("/top")
    List<TopEmployer> top(@RequestParam(defaultValue = "5") int limit) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED)
                    .field("limit", "Must be between 1 and " + MAX_LIMIT + ".");
        }
        // v_open_vacancy already requires an approved employer with an active account.
        return jdbc.sql("""
                SELECT e.employer_id, e.business_name, c.code, c.name_en, c.name_sw, COUNT(*) open_jobs
                  FROM v_open_vacancy v
                  JOIN employer e ON e.employer_id = v.employer_id
                  LEFT JOIN job_category c ON c.category_id = e.category_id
                 WHERE e.deleted_at IS NULL
                 GROUP BY e.employer_id, e.business_name, c.code, c.name_en, c.name_sw
                 ORDER BY open_jobs DESC, e.business_name
                 FETCH FIRST :limit ROWS ONLY
                """)
                .param("limit", limit)
                .query((rs, n) -> {
                    String name = rs.getString("business_name");
                    return new TopEmployer(rs.getLong("employer_id"), name, Initials.of(name),
                            LookupRef.ofNullable(rs.getString("code"), rs.getString("name_en"), rs.getString("name_sw")),
                            rs.getInt("open_jobs"));
                })
                .list();
    }
}
