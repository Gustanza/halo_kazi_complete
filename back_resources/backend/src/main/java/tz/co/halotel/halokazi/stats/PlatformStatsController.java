package tz.co.halotel.halokazi.stats;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Live headline numbers for the landing page. */
@RestController
@RequestMapping("/api/public/stats")
class PlatformStatsController {

    record PlatformStats(long openJobs, long employers, long jobSeekers, long jobsPostedLast30Days) {
    }

    private final JdbcClient jdbc;

    PlatformStatsController(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    PlatformStats get() {
        return jdbc.sql("""
                SELECT (SELECT COUNT(*) FROM v_open_vacancy) open_jobs,
                       (SELECT COUNT(*) FROM employer e JOIN app_user u ON u.user_id = e.user_id
                         WHERE e.verification_status = 'APPROVED' AND u.account_status = 'ACTIVE'
                           AND e.deleted_at IS NULL) employers,
                       (SELECT COUNT(*) FROM app_user
                         WHERE role = 'JOB_SEEKER' AND account_status = 'ACTIVE') job_seekers,
                       (SELECT COUNT(*) FROM vacancy
                         WHERE published_at >= SYSTIMESTAMP - NUMTODSINTERVAL(30, 'DAY')
                           AND status <> 'DELETED') jobs_posted_30d
                  FROM dual
                """)
                .query((rs, n) -> new PlatformStats(rs.getLong("open_jobs"), rs.getLong("employers"),
                        rs.getLong("job_seekers"), rs.getLong("jobs_posted_30d")))
                .single();
    }
}
