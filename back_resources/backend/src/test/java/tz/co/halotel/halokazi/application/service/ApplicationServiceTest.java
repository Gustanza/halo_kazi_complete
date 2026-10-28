package tz.co.halotel.halokazi.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import tz.co.halotel.halokazi.application.dto.ApplicationSubmitRequest;
import tz.co.halotel.halokazi.application.dto.ApplicationSummaryDto;
import tz.co.halotel.halokazi.application.entity.Application;
import tz.co.halotel.halokazi.application.entity.Cv;
import tz.co.halotel.halokazi.application.entity.StoredFile;
import tz.co.halotel.halokazi.application.repository.ApplicationRepository;
import tz.co.halotel.halokazi.application.repository.ApplicationStatusHistoryRepository;
import tz.co.halotel.halokazi.application.repository.CvRepository;
import tz.co.halotel.halokazi.application.repository.StoredFileRepository;
import tz.co.halotel.halokazi.common.exception.BadRequestException;
import tz.co.halotel.halokazi.vacancy.entity.Employer;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;
import tz.co.halotel.halokazi.vacancy.repository.VacancyRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationStatusHistoryRepository historyRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private CvRepository cvRepository;

    @Mock
    private StoredFileRepository storedFileRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private jakarta.persistence.EntityManager entityManager;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Vacancy activeVacancy;

    @BeforeEach
    void setUp() {
        Employer emp = new Employer();
        emp.setEmployerId(10L);
        emp.setBusinessName("Halotel");

        activeVacancy = new Vacancy();
        activeVacancy.setVacancyId(100L);
        activeVacancy.setJobTitle("Software Engineer");
        activeVacancy.setStatus("ACTIVE");
        activeVacancy.setEmployer(emp);
        activeVacancy.setApplicationDeadline(OffsetDateTime.now().plusDays(5));
    }

    @Test
    void submitApplication_Valid_ShouldSaveApplication() {
        ApplicationSubmitRequest request = new ApplicationSubmitRequest();
        request.setVacancyId(100L);
        request.setCvId(10L);
        request.setCoverLetter("I am an experienced engineer");
        request.setRequirementsConfirmed(true);

        when(vacancyRepository.findByVacancyIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(activeVacancy));
        when(applicationRepository.existsByProfileIdAndVacancyVacancyIdAndStatusNot(1L, 100L, "REJECTED")).thenReturn(false);

        // Mock CV
        Cv mockCv = new Cv(1L, 1L, "My Resume");
        when(cvRepository.findByCvIdAndProfileIdAndDeletedAtIsNull(10L, 1L)).thenReturn(Optional.of(mockCv));

        // Mock StoredFile
        StoredFile mockFile = new StoredFile(2L, "APPLICATION_CV_PDF", "key", "test.pdf", "application/pdf", 1024L);
        when(storedFileRepository.save(any(StoredFile.class))).thenReturn(mockFile);

        Application savedApp = new Application();
        savedApp.setApplicationId(1L);
        savedApp.setApplicationRef("HK-2026-000001");
        savedApp.setVacancy(activeVacancy);
        savedApp.setStatus("SUBMITTED");
        savedApp.setSubmittedAt(OffsetDateTime.now());

        when(applicationRepository.saveAndFlush(any(Application.class))).thenReturn(savedApp);

        ApplicationSummaryDto result = applicationService.submitApplication(1L, 2L, request);

        assertNotNull(result);
        assertEquals("SUBMITTED", result.getStatus());
        assertEquals("HK-2026-000001", result.getApplicationRef());
        verify(applicationRepository, times(1)).saveAndFlush(any(Application.class));
    }

    @Test
    void submitApplication_AlreadyApplied_ShouldThrowBadRequest() {
        ApplicationSubmitRequest request = new ApplicationSubmitRequest();
        request.setVacancyId(100L);
        request.setCoverLetter("Hello");
        request.setRequirementsConfirmed(true);

        when(vacancyRepository.findByVacancyIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(activeVacancy));
        when(applicationRepository.existsByProfileIdAndVacancyVacancyIdAndStatusNot(1L, 100L, "REJECTED")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> applicationService.submitApplication(1L, 2L, request));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void submitApplication_ExpiredDeadline_ShouldThrowBadRequest() {
        activeVacancy.setApplicationDeadline(OffsetDateTime.now().minusDays(1));

        ApplicationSubmitRequest request = new ApplicationSubmitRequest();
        request.setVacancyId(100L);
        request.setCoverLetter("Hello");
        request.setRequirementsConfirmed(true);

        when(vacancyRepository.findByVacancyIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(activeVacancy));

        assertThrows(BadRequestException.class, () -> applicationService.submitApplication(1L, 2L, request));
    }
}
