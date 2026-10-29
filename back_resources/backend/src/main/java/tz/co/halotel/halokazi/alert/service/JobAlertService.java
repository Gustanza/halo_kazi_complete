package tz.co.halotel.halokazi.alert.service;

import tz.co.halotel.halokazi.alert.dto.JobAlertPreferencesDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;

import java.util.List;

public interface JobAlertService {

    JobAlertPreferencesDto getPreferences(Long profileId);

    void updatePreferences(Long profileId, JobAlertPreferencesDto dto);

    List<VacancySummaryDto> getMatchedAlertVacancies(Long profileId);
}
