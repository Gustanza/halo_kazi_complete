package tz.co.halotel.halokazi.vacancy.service;

import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.vacancy.dto.LookupDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancyDetailDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancyFilterParams;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;

import java.util.List;

public interface VacancyService {

    PageResponse<VacancySummaryDto> searchVacancies(VacancyFilterParams params, Long currentProfileId);

    VacancyDetailDto getVacancyDetails(Long id, Long currentProfileId);

    List<LookupDto> getCategories();

    List<LookupDto> getEmploymentTypes();

    List<LookupDto> getRegions();

    List<LookupDto> getDistricts(Long regionId);
}
