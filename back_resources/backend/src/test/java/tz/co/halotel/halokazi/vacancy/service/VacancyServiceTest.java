package tz.co.halotel.halokazi.vacancy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.vacancy.dto.LookupDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancyDetailDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancyFilterParams;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;
import tz.co.halotel.halokazi.vacancy.entity.*;
import tz.co.halotel.halokazi.vacancy.repository.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private JobCategoryRepository categoryRepository;

    @Mock
    private EmploymentTypeRepository employmentTypeRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    private Vacancy sampleVacancy;
    private Employer sampleEmployer;
    private Region sampleRegion;

    @BeforeEach
    void setUp() {
        sampleEmployer = new Employer();
        sampleEmployer.setEmployerId(10L);
        sampleEmployer.setBusinessName("Halotel Tanzania PLC");

        sampleRegion = new Region();
        sampleRegion.setRegionId(1L);
        sampleRegion.setNameEn("Dar es Salaam");
        sampleRegion.setCode("DSM");

        sampleVacancy = new Vacancy();
        sampleVacancy.setVacancyId(101L);
        sampleVacancy.setJobTitle("Software Engineer");
        sampleVacancy.setEmployer(sampleEmployer);
        sampleVacancy.setRegion(sampleRegion);
        sampleVacancy.setJobDescription("Develop backend APIs");
        sampleVacancy.setJobRequirements("Java, Spring Boot");
        sampleVacancy.setQualifications("BSc in Computer Science");
        sampleVacancy.setWorkExperience("2+ years experience");
        sampleVacancy.setMinExperienceYears(2.0);
        sampleVacancy.setNumberOfPositions(3);
        sampleVacancy.setApplicationDeadline(OffsetDateTime.now().plusDays(10));
        sampleVacancy.setPublishedAt(OffsetDateTime.now().minusDays(1));
        sampleVacancy.setStatus("ACTIVE");
    }

    @Test
    void searchVacancies_ShouldReturnPaginatedResults() {
        VacancyFilterParams params = new VacancyFilterParams();
        params.setKeyword("Software");

        Page<Vacancy> page = new PageImpl<>(List.of(sampleVacancy));
        when(vacancyRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<VacancySummaryDto> result = vacancyService.searchVacancies(params, 5L);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Software Engineer", result.getContent().get(0).getTitle());
        assertEquals("Halotel Tanzania PLC", result.getContent().get(0).getCompany());
        verify(vacancyRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getVacancyDetails_ExistingId_ShouldReturnDetails() {
        when(vacancyRepository.findByVacancyIdAndDeletedAtIsNull(101L)).thenReturn(Optional.of(sampleVacancy));

        VacancyDetailDto details = vacancyService.getVacancyDetails(101L, 5L);

        assertNotNull(details);
        assertEquals(101L, details.getId());
        assertEquals("Software Engineer", details.getTitle());
        assertEquals("Develop backend APIs", details.getDescription());
    }

    @Test
    void getVacancyDetails_NonExistentId_ShouldThrowException() {
        when(vacancyRepository.findByVacancyIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vacancyService.getVacancyDetails(999L, 5L));
    }

    @Test
    void getCategories_ShouldReturnActiveCategories() {
        JobCategory cat = new JobCategory();
        cat.setCategoryId(2L);
        cat.setCode("IT_TECH");
        cat.setNameEn("Information Technology");
        cat.setNameSw("Teknolojia ya Habari");

        when(categoryRepository.findByIsActiveOrderBySortOrderAsc(1)).thenReturn(List.of(cat));

        List<LookupDto> list = vacancyService.getCategories();
        assertEquals(1, list.size());
        assertEquals("IT_TECH", list.get(0).getCode());
    }
}
