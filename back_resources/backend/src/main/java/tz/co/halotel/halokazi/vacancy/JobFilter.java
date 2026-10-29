package tz.co.halotel.halokazi.vacancy;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.lookup.FilterOption;

/**
 * A fixed job filter band. Each constant owns its SQL predicate over the {@code v} (v_open_vacancy)
 * alias; predicates use only constants, never request input.
 */
public interface JobFilter {

    String label();

    String predicate();

    String name();

    default FilterOption toOption() {
        return new FilterOption(name(), label());
    }

    static <E extends Enum<E> & JobFilter> List<FilterOption> options(Class<E> type) {
        return Arrays.stream(type.getEnumConstants()).map(JobFilter::toOption).toList();
    }

    /** Case-insensitive; null/blank means "no filter". Unknown values are a 400 on {@code param}. */
    static <E extends Enum<E> & JobFilter> E parse(Class<E> type, String param, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED).field(param,
                    "Unknown value. Use one of: " + String.join(", ",
                            Arrays.stream(type.getEnumConstants()).map(Enum::name).toList()) + ".");
        }
    }

    /** Required experience, from vacancy.min_experience_years. */
    enum ExperienceLevel implements JobFilter {
        ENTRY("Entry level (under 1 year)", "v.min_experience_years < 1"),
        JUNIOR("1-3 years", "v.min_experience_years >= 1 AND v.min_experience_years < 3"),
        MID("3-5 years", "v.min_experience_years >= 3 AND v.min_experience_years < 5"),
        SENIOR("5+ years", "v.min_experience_years >= 5");

        private final String label;
        private final String predicate;

        ExperienceLevel(String label, String predicate) {
            this.label = label;
            this.predicate = predicate;
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String predicate() {
            return predicate;
        }
    }

    /**
     * Monthly TZS salary bands. A vacancy matches when its advertised range overlaps the band;
     * vacancies without a monthly TZS salary never match a salary filter.
     */
    enum SalaryRange implements JobFilter {
        UNDER_500K("Under TZS 500,000", 0, 500_000L),
        FROM_500K_TO_1M("TZS 500,000 - 1,000,000", 500_000L, 1_000_000L),
        FROM_1M_TO_3M("TZS 1,000,000 - 3,000,000", 1_000_000L, 3_000_000L),
        OVER_3M("Over TZS 3,000,000", 3_000_000L, null);

        private final String label;
        private final String predicate;

        SalaryRange(String label, long from, Long below) {
            this.label = label;
            this.predicate = "v.salary_period = 'MONTH' AND v.salary_currency = 'TZS'"
                    + " AND NVL(v.salary_max, v.salary_min) >= " + from
                    + (below == null ? "" : " AND NVL(v.salary_min, v.salary_max) < " + below);
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String predicate() {
            return predicate;
        }
    }

    /** How recently the vacancy was published. */
    enum DatePosted implements JobFilter {
        LAST_24_HOURS("Last 24 hours", 1),
        LAST_7_DAYS("Last 7 days", 7),
        LAST_30_DAYS("Last 30 days", 30);

        private final String label;
        private final String predicate;

        DatePosted(String label, int days) {
            this.label = label;
            this.predicate = "v.published_at >= SYSTIMESTAMP - NUMTODSINTERVAL(" + days + ", 'DAY')";
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String predicate() {
            return predicate;
        }
    }
}
