package tz.co.halotel.halokazi.saved.service;

import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;

import java.util.List;

public interface SavedJobService {

    List<VacancySummaryDto> getSavedJobs(Long profileId);

    void saveVacancy(Long profileId, Long vacancyId);

    void unsaveVacancy(Long profileId, Long vacancyId);

    boolean isSaved(Long profileId, Long vacancyId);
}
