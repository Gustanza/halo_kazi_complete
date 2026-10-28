package tz.co.halotel.halokazi.saved.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.saved.entity.SavedVacancy;
import tz.co.halotel.halokazi.saved.repository.SavedVacancyRepository;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;
import tz.co.halotel.halokazi.vacancy.entity.Employer;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;
import tz.co.halotel.halokazi.vacancy.repository.VacancyRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedJobServiceTest {

    @Mock
    private SavedVacancyRepository savedVacancyRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @InjectMocks
    private SavedJobServiceImpl savedJobService;

    private Vacancy sampleVacancy;

    @BeforeEach
    void setUp() {
        Employer emp = new Employer();
        emp.setEmployerId(10L);
        emp.setBusinessName("Halotel");

        sampleVacancy = new Vacancy();
        sampleVacancy.setVacancyId(100L);
        sampleVacancy.setJobTitle("Backend Developer");
        sampleVacancy.setEmployer(emp);
        sampleVacancy.setApplicationDeadline(OffsetDateTime.now().plusDays(5));
        sampleVacancy.setNumberOfPositions(1);
    }

    @Test
    void saveVacancy_NewVacancy_ShouldSave() {
        when(vacancyRepository.findByVacancyIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(sampleVacancy));
        when(savedVacancyRepository.existsByIdProfileIdAndIdVacancyId(1L, 100L)).thenReturn(false);

        savedJobService.saveVacancy(1L, 100L);

        verify(savedVacancyRepository, times(1)).save(any(SavedVacancy.class));
    }

    @Test
    void saveVacancy_AlreadySaved_ShouldNotDuplicate() {
        when(vacancyRepository.findByVacancyIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(sampleVacancy));
        when(savedVacancyRepository.existsByIdProfileIdAndIdVacancyId(1L, 100L)).thenReturn(true);

        savedJobService.saveVacancy(1L, 100L);

        verify(savedVacancyRepository, never()).save(any(SavedVacancy.class));
    }

    @Test
    void saveVacancy_NonExistent_ShouldThrowException() {
        when(vacancyRepository.findByVacancyIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> savedJobService.saveVacancy(1L, 999L));
    }

    @Test
    void unsaveVacancy_Existing_ShouldDelete() {
        when(savedVacancyRepository.existsByIdProfileIdAndIdVacancyId(1L, 100L)).thenReturn(true);

        savedJobService.unsaveVacancy(1L, 100L);

        verify(savedVacancyRepository, times(1)).deleteByIdProfileIdAndIdVacancyId(1L, 100L);
    }

    @Test
    void isSaved_ShouldReturnStatus() {
        when(savedVacancyRepository.existsByIdProfileIdAndIdVacancyId(1L, 100L)).thenReturn(true);
        assertTrue(savedJobService.isSaved(1L, 100L));

        when(savedVacancyRepository.existsByIdProfileIdAndIdVacancyId(1L, 200L)).thenReturn(false);
        assertFalse(savedJobService.isSaved(1L, 200L));
    }
}
