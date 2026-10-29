package tz.co.halotel.halokazi.vacancy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.vacancy.dto.LookupDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancyDetailDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancyFilterParams;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;
import tz.co.halotel.halokazi.vacancy.repository.*;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final JobCategoryRepository categoryRepository;
    private final EmploymentTypeRepository employmentTypeRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String[] PALETTE = {
            "#FD5C20", "#1F7A3F", "#F6861F", "#2F8FF5", "#B064F7", "#E3001B", "#0D9488"
    };

    public VacancyServiceImpl(VacancyRepository vacancyRepository,
                              JobCategoryRepository categoryRepository,
                              EmploymentTypeRepository employmentTypeRepository,
                              RegionRepository regionRepository,
                              DistrictRepository districtRepository,
                              JdbcTemplate jdbcTemplate) {
        this.vacancyRepository = vacancyRepository;
        this.categoryRepository = categoryRepository;
        this.employmentTypeRepository = employmentTypeRepository;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PageResponse<VacancySummaryDto> searchVacancies(VacancyFilterParams params, Long currentProfileId) {
        Pageable pageable = createPageable(params);
        Specification<Vacancy> spec = VacancySpecification.filterOpenVacancies(params);

        Page<Vacancy> page = vacancyRepository.findAll(spec, pageable);
        List<VacancySummaryDto> dtos = page.getContent().stream()
                .map(v -> mapToSummaryDto(v, currentProfileId))
                .collect(Collectors.toList());

        return new PageResponse<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public VacancyDetailDto getVacancyDetails(Long id, Long currentProfileId) {
        Vacancy vacancy = vacancyRepository.findByVacancyIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy", "id", id));

        return mapToDetailDto(vacancy, currentProfileId);
    }

    @Override
    public List<LookupDto> getCategories() {
        return categoryRepository.findByIsActiveOrderBySortOrderAsc(1).stream()
                .map(c -> new LookupDto(c.getCategoryId(), c.getCode(), c.getNameEn(), c.getNameSw()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LookupDto> getEmploymentTypes() {
        return employmentTypeRepository.findByIsActiveOrderBySortOrderAsc(1).stream()
                .map(e -> new LookupDto(e.getEmploymentTypeId(), e.getCode(), e.getNameEn(), e.getNameSw()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LookupDto> getRegions() {
        return regionRepository.findByIsActiveOrderByNameEnAsc(1).stream()
                .map(r -> new LookupDto(r.getRegionId(), r.getCode(), r.getNameEn(), r.getNameSw()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LookupDto> getDistricts(Long regionId) {
        return districtRepository.findByRegionRegionIdAndIsActiveOrderByNameEnAsc(regionId, 1).stream()
                .map(d -> new LookupDto(d.getDistrictId(), d.getNameEn(), d.getNameEn(), d.getNameSw()))
                .collect(Collectors.toList());
    }

    private Pageable createPageable(VacancyFilterParams params) {
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedAt");
        if (params.getSort() != null && !params.getSort().isBlank()) {
            String[] parts = params.getSort().split(",");
            String prop = parts[0].trim();
            Sort.Direction dir = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;

            // Whitelist valid sort properties
            if (prop.equalsIgnoreCase("deadline") || prop.equalsIgnoreCase("applicationDeadline")) {
                sort = Sort.by(dir, "applicationDeadline");
            } else if (prop.equalsIgnoreCase("title") || prop.equalsIgnoreCase("jobTitle")) {
                sort = Sort.by(dir, "jobTitle");
            } else {
                sort = Sort.by(dir, "publishedAt");
            }
        }
        return PageRequest.of(params.getPage(), params.getSize(), sort);
    }

    private VacancySummaryDto mapToSummaryDto(Vacancy v, Long currentProfileId) {
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

        if (currentProfileId != null) {
            dto.setSaved(isVacancySaved(currentProfileId, v.getVacancyId()));
        }

        return dto;
    }

    private VacancyDetailDto mapToDetailDto(Vacancy v, Long currentProfileId) {
        VacancyDetailDto dto = new VacancyDetailDto();
        // Copy summary fields
        VacancySummaryDto summary = mapToSummaryDto(v, currentProfileId);
        dto.setId(summary.getId());
        dto.setTitle(summary.getTitle());
        dto.setCompany(summary.getCompany());
        dto.setCompanyId(summary.getCompanyId());
        dto.setInitials(summary.getInitials());
        dto.setBrandColor(summary.getBrandColor());
        dto.setIndustry(summary.getIndustry());
        dto.setCategoryId(summary.getCategoryId());
        dto.setLocation(summary.getLocation());
        dto.setRegionId(summary.getRegionId());
        dto.setType(summary.getType());
        dto.setEmploymentTypeId(summary.getEmploymentTypeId());
        dto.setSalaryDisplay(summary.getSalaryDisplay());
        dto.setPublishedAt(summary.getPublishedAt());
        dto.setDeadline(summary.getDeadline());
        dto.setPositions(summary.getPositions());
        dto.setNew(summary.isNew());
        dto.setSaved(summary.isSaved());

        // Detailed fields
        dto.setDescription(v.getJobDescription());
        dto.setRequirements(v.getJobRequirements());
        dto.setQualifications(v.getQualifications());

        if (v.getMinEducationLevel() != null) {
            dto.setMinEducationLevel(v.getMinEducationLevel().getNameEn());
            dto.setMinEducationLevelId(v.getMinEducationLevel().getEducationLevelId());
        }

        dto.setWorkExperience(v.getWorkExperience());
        dto.setMinExperienceYears(v.getMinExperienceYears());

        if (v.getDistrict() != null) {
            dto.setDistrict(v.getDistrict().getNameEn());
            dto.setDistrictId(v.getDistrict().getDistrictId());
        }

        dto.setLocationDetails(v.getLocationDetails());
        dto.setStatus(v.getStatus());

        if (v.getEmployer() != null) {
            dto.setCompanyDescription(v.getEmployer().getCompanyDescription());
            dto.setCompanyWebsite(v.getEmployer().getWebsite());
            dto.setCompanyEmail(v.getEmployer().getCompanyEmail());
            dto.setCompanyPhone(v.getEmployer().getCompanyPhone());
        }

        if (currentProfileId != null) {
            dto.setHasApplied(hasUserApplied(currentProfileId, v.getVacancyId()));
        }

        return dto;
    }

    private boolean isVacancySaved(Long profileId, Long vacancyId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM saved_vacancy WHERE profile_id = ? AND vacancy_id = ?",
                    Integer.class,
                    profileId, vacancyId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasUserApplied(Long profileId, Long vacancyId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM application WHERE profile_id = ? AND vacancy_id = ? AND status <> 'REJECTED'",
                    Integer.class,
                    profileId, vacancyId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
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
