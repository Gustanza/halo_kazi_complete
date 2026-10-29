package tz.co.halotel.halokazi.vacancy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import tz.co.halotel.halokazi.common.util.Initials;
import tz.co.halotel.halokazi.common.util.SqlRows;
import tz.co.halotel.halokazi.common.web.PageQuery;
import tz.co.halotel.halokazi.common.web.PageResponse;
import tz.co.halotel.halokazi.lookup.LookupRef;
import tz.co.halotel.halokazi.vacancy.JobViews.Company;
import tz.co.halotel.halokazi.vacancy.JobViews.JobDetail;
import tz.co.halotel.halokazi.vacancy.JobViews.JobSummary;
import tz.co.halotel.halokazi.vacancy.JobViews.Salary;

/**
 * Read model over v_open_vacancy, which already limits rows to ACTIVE vacancies before their deadline
 * from approved, active employers (VAC-06, VAC-11). Keeping that rule in the view means the API can
 * never show a job the database considers closed.
 */
@Repository
class JobQueries {

    private static final String FROM = """
            FROM v_open_vacancy v
            JOIN employer e        ON e.employer_id = v.employer_id
            JOIN region r          ON r.region_id = v.region_id
            JOIN employment_type t ON t.employment_type_id = v.employment_type_id
            LEFT JOIN job_category c ON c.category_id = v.category_id
            """;

    private static final String CARD_COLUMNS = """
            SELECT v.vacancy_id, v.job_title, v.published_at, v.application_deadline,
                   v.salary_min, v.salary_max, v.salary_currency, v.salary_period,
                   e.employer_id, e.business_name,
                   r.code r_code, r.name_en r_en, r.name_sw r_sw,
                   t.code t_code, t.name_en t_en, t.name_sw t_sw,
                   c.code c_code, c.name_en c_en, c.name_sw c_sw
            """;

    private final JdbcClient jdbc;

    JobQueries(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    PageResponse<JobSummary> search(JobSearch search, PageQuery page) {
        Where where = Where.of(search);
        long total = jdbc.sql("SELECT COUNT(*) " + FROM + where.sql())
                .params(where.params())
                .query(Long.class)
                .single();
        List<JobSummary> items = total == 0 ? List.of() : jdbc.sql(CARD_COLUMNS + FROM + where.sql() + """
                ORDER BY v.published_at DESC, v.vacancy_id DESC
                OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY
                """)
                .params(where.params())
                .param("offset", page.offset())
                .param("size", page.size())
                .query((rs, n) -> summary(rs))
                .list();
        return PageResponse.of(items, page, total);
    }

    Optional<JobDetail> findOpen(long id) {
        return jdbc.sql(CARD_COLUMNS + """
                     , v.job_description, v.job_requirements, v.qualifications, v.work_experience,
                       v.min_experience_years, v.number_of_positions, v.location_details,
                       d.name_en d_name,
                       l.code l_code, l.name_en l_en, l.name_sw l_sw
                """ + FROM + """
                LEFT JOIN district d        ON d.district_id = v.district_id
                LEFT JOIN education_level l ON l.education_level_id = v.min_education_level_id
                WHERE v.vacancy_id = :id
                """)
                .param("id", id)
                .query((rs, n) -> detail(rs))
                .optional();
    }

    private static JobSummary summary(ResultSet rs) throws SQLException {
        return new JobSummary(rs.getLong("vacancy_id"), rs.getString("job_title"), company(rs),
                region(rs), employmentType(rs), category(rs), salary(rs),
                SqlRows.utc(rs, "published_at"), SqlRows.utc(rs, "application_deadline"));
    }

    private static JobDetail detail(ResultSet rs) throws SQLException {
        return new JobDetail(rs.getLong("vacancy_id"), rs.getString("job_title"), company(rs),
                region(rs), rs.getString("d_name"), rs.getString("location_details"),
                employmentType(rs), category(rs), salary(rs),
                rs.getString("job_description"), rs.getString("job_requirements"), rs.getString("qualifications"),
                rs.getString("work_experience"), SqlRows.decimal(rs, "min_experience_years"),
                LookupRef.ofNullable(rs.getString("l_code"), rs.getString("l_en"), rs.getString("l_sw")),
                rs.getInt("number_of_positions"),
                SqlRows.utc(rs, "published_at"), SqlRows.utc(rs, "application_deadline"));
    }

    private static Company company(ResultSet rs) throws SQLException {
        String name = rs.getString("business_name");
        return new Company(rs.getLong("employer_id"), name, Initials.of(name));
    }

    private static LookupRef region(ResultSet rs) throws SQLException {
        return new LookupRef(rs.getString("r_code"), rs.getString("r_en"), rs.getString("r_sw"));
    }

    private static LookupRef employmentType(ResultSet rs) throws SQLException {
        return new LookupRef(rs.getString("t_code"), rs.getString("t_en"), rs.getString("t_sw"));
    }

    private static LookupRef category(ResultSet rs) throws SQLException {
        return LookupRef.ofNullable(rs.getString("c_code"), rs.getString("c_en"), rs.getString("c_sw"));
    }

    private static Salary salary(ResultSet rs) throws SQLException {
        var min = SqlRows.decimal(rs, "salary_min");
        var max = SqlRows.decimal(rs, "salary_max");
        if (min == null && max == null) {
            return null;
        }
        return new Salary(min, max, rs.getString("salary_currency"), rs.getString("salary_period"));
    }

    /** WHERE clause built from fixed fragments; request values only ever travel as bind parameters. */
    private record Where(String sql, Map<String, Object> params) {

        static Where of(JobSearch s) {
            List<String> clauses = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();
            if (s.keyword() != null) {
                clauses.add("(LOWER(v.job_title) LIKE :kw ESCAPE '\\' OR LOWER(e.business_name) LIKE :kw ESCAPE '\\')");
                params.put("kw", "%" + escapeLike(s.keyword().toLowerCase(Locale.ROOT)) + "%");
            }
            if (s.regionCode() != null) {
                clauses.add("r.code = :region");
                params.put("region", s.regionCode());
            }
            if (s.employmentTypeCode() != null) {
                clauses.add("t.code = :type");
                params.put("type", s.employmentTypeCode());
            }
            if (s.categoryCode() != null) {
                clauses.add("c.code = :category");
                params.put("category", s.categoryCode());
            }
            for (JobFilter band : new JobFilter[] { s.experience(), s.salary(), s.posted() }) {
                if (band != null) {
                    clauses.add(band.predicate());
                }
            }
            String sql = clauses.isEmpty() ? "" : "WHERE " + String.join(" AND ", clauses) + "\n";
            return new Where(sql, params);
        }

        private static String escapeLike(String value) {
            return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        }
    }
}
