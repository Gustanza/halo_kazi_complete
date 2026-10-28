package tz.co.halotel.halokazi.alert.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.halotel.halokazi.alert.dto.JobAlertPreferencesDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;
import tz.co.halotel.halokazi.vacancy.repository.VacancyRepository;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobAlertServiceImpl implements JobAlertService {

    private final JdbcTemplate jdbcTemplate;
    private final VacancyRepository vacancyRepository;

    private static final String[] PALETTE = {
            "#FD5C20", "#1F7A3F", "#F6861F", "#2F8FF5", "#B064F7", "#E3001B", "#0D9488"
    };

    public JobAlertServiceImpl(JdbcTemplate jdbcTemplate, VacancyRepository vacancyRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.vacancyRepository = vacancyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public JobAlertPreferencesDto getPreferences(Long profileId) {
        Boolean receiveAlerts = true;
        try {
            Integer val = jdbcTemplate.queryForObject(
                    "SELECT receive_job_alerts FROM job_seeker_profile WHERE profile_id = ?",
                    Integer.class,
                    profileId);
            if (val != null) {
                receiveAlerts = (val == 1);
            }
        } catch (Exception ignored) {}

        List<Long> categories = jdbcTemplate.query(
                "SELECT category_id FROM profile_preferred_category WHERE profile_id = ?",
                (rs, rowNum) -> rs.getLong("category_id"),
                profileId);

        List<Long> regions = jdbcTemplate.query(
                "SELECT region_id FROM profile_preferred_region WHERE profile_id = ?",
                (rs, rowNum) -> rs.getLong("region_id"),
                profileId);

        List<Long> empTypes = jdbcTemplate.query(
                "SELECT employment_type_id FROM profile_preferred_emp_type WHERE profile_id = ?",
                (rs, rowNum) -> rs.getLong("employment_type_id"),
                profileId);

        return new JobAlertPreferencesDto(receiveAlerts, categories, regions, empTypes);
    }

    @Override
    public void updatePreferences(Long profileId, JobAlertPreferencesDto dto) {
        int alertFlag = dto.isReceiveJobAlerts() ? 1 : 0;
        jdbcTemplate.update(
                "UPDATE job_seeker_profile SET receive_job_alerts = ?, updated_at = SYSTIMESTAMP WHERE profile_id = ?",
                alertFlag, profileId);

        // Update preferred categories
        jdbcTemplate.update("DELETE FROM profile_preferred_category WHERE profile_id = ?", profileId);
        if (dto.getCategoryIds() != null) {
            for (Long catId : dto.getCategoryIds()) {
                jdbcTemplate.update("INSERT INTO profile_preferred_category (profile_id, category_id) VALUES (?, ?)", profileId, catId);
            }
        }

        // Update preferred regions
        jdbcTemplate.update("DELETE FROM profile_preferred_region WHERE profile_id = ?", profileId);
        if (dto.getRegionIds() != null) {
            for (Long regId : dto.getRegionIds()) {
                jdbcTemplate.update("INSERT INTO profile_preferred_region (profile_id, region_id) VALUES (?, ?)", profileId, regId);
            }
        }

        // Update preferred employment types
        jdbcTemplate.update("DELETE FROM profile_preferred_emp_type WHERE profile_id = ?", profileId);
        if (dto.getEmploymentTypeIds() != null) {
            for (Long empId : dto.getEmploymentTypeIds()) {
                jdbcTemplate.update("INSERT INTO profile_preferred_emp_type (profile_id, employment_type_id) VALUES (?, ?)", profileId, empId);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacancySummaryDto> getMatchedAlertVacancies(Long profileId) {
        String sql = "SELECT m.vacancy_id FROM v_vacancy_profile_match m " +
                "WHERE m.profile_id = ? " +
                "  AND (m.category_match = 1 OR m.region_match = 1 OR m.employment_type_match = 1 OR m.matched_skills > 0) " +
                "ORDER BY (m.category_match * 3 + m.region_match * 2 + m.employment_type_match + m.matched_skills) DESC " +
                "FETCH FIRST 15 ROWS ONLY";

        List<Long> matchedIds;
        try {
            matchedIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("vacancy_id"), profileId);
        } catch (Exception e) {
            matchedIds = Collections.emptyList();
        }

        if (matchedIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Vacancy> vacancies = vacancyRepository.findAllById(matchedIds);
        return vacancies.stream()
                .map(v -> mapToSummaryDto(v, profileId))
                .collect(Collectors.toList());
    }

    private VacancySummaryDto mapToSummaryDto(Vacancy v, Long profileId) {
        VacancySummaryDto dto = new VacancySummaryDto();
        dto.setId(v.getVacancyId());
        dto.setTitle(v.getJobTitle());

        if (v.getEmployer() != null) {
            String companyName = v.getEmployer().getBusinessName();
            dto.setCompany(companyName);
            dto.setCompanyId(v.getEmployer().getEmployerId());
            dto.setInitials(deriveInitials(companyName));
            dto.setBrandColor(deriveBrandColor(companyName));
        }

        if (v.getCategory() != null) {
            dto.setIndustry(v.getCategory().getNameEn());
            dto.setCategoryId(v.getCategory().getCategoryId());
        }

        if (v.getRegion() != null) {
            dto.setLocation(v.getRegion().getNameEn());
            dto.setRegionId(v.getRegion().getRegionId());
        }

        if (v.getEmploymentType() != null) {
            dto.setType(v.getEmploymentType().getNameEn());
            dto.setEmploymentTypeId(v.getEmploymentType().getEmploymentTypeId());
        }

        dto.setSalaryDisplay(formatSalary(v));
        dto.setPublishedAt(v.getPublishedAt());
        dto.setDeadline(v.getApplicationDeadline());
        dto.setPositions(v.getNumberOfPositions());

        if (v.getPublishedAt() != null) {
            dto.setNew(v.getPublishedAt().isAfter(OffsetDateTime.now().minusDays(7)));
        }

        return dto;
    }

    private String formatSalary(Vacancy v) {
        if (v.getSalaryMin() == null && v.getSalaryMax() == null) {
            return "Competitive";
        }
        DecimalFormat df = new DecimalFormat("#,###");
        String curr = v.getSalaryCurrency() != null ? v.getSalaryCurrency() : "TZS";
        String period = v.getSalaryPeriod() != null ? "/" + v.getSalaryPeriod().toLowerCase() : "";

        if (v.getSalaryMin() != null && v.getSalaryMax() != null) {
            return curr + " " + df.format(v.getSalaryMin()) + " - " + df.format(v.getSalaryMax()) + " " + period;
        } else if (v.getSalaryMin() != null) {
            return "From " + curr + " " + df.format(v.getSalaryMin()) + " " + period;
        } else {
            return "Up to " + curr + " " + df.format(v.getSalaryMax()) + " " + period;
        }
    }

    private String deriveInitials(String name) {
        if (name == null || name.isBlank()) return "HK";
        String[] words = name.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, Math.min(3, words[0].length())).toUpperCase();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, words.length); i++) {
            if (!words[i].isEmpty()) {
                sb.append(Character.toUpperCase(words[i].charAt(0)));
            }
        }
        return sb.toString();
    }

    private String deriveBrandColor(String name) {
        if (name == null || name.isBlank()) return PALETTE[0];
        int hash = Math.abs(name.hashCode());
        return PALETTE[hash % PALETTE.length];
    }
}
