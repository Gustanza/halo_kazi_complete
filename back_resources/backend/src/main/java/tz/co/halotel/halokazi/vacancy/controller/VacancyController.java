package tz.co.halotel.halokazi.vacancy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.api.ApiResponse;
import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.common.security.CurrentUser;
import tz.co.halotel.halokazi.common.security.UserPrincipal;
import tz.co.halotel.halokazi.vacancy.dto.LookupDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancyDetailDto;
import tz.co.halotel.halokazi.vacancy.dto.VacancyFilterParams;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;
import tz.co.halotel.halokazi.vacancy.service.VacancyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VacancySummaryDto>>> searchVacancies(
            VacancyFilterParams params,
            @CurrentUser(required = false) UserPrincipal currentUser) {

        Long profileId = currentUser != null ? currentUser.getProfileId() : null;
        PageResponse<VacancySummaryDto> result = vacancyService.searchVacancies(params, profileId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VacancyDetailDto>> getVacancyById(
            @PathVariable("id") Long id,
            @CurrentUser(required = false) UserPrincipal currentUser) {

        Long profileId = currentUser != null ? currentUser.getProfileId() : null;
        VacancyDetailDto dto = vacancyService.getVacancyDetails(id, profileId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/meta/categories")
    public ResponseEntity<ApiResponse<List<LookupDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(vacancyService.getCategories()));
    }

    @GetMapping("/meta/employment-types")
    public ResponseEntity<ApiResponse<List<LookupDto>>> getEmploymentTypes() {
        return ResponseEntity.ok(ApiResponse.success(vacancyService.getEmploymentTypes()));
    }

    @GetMapping("/meta/regions")
    public ResponseEntity<ApiResponse<List<LookupDto>>> getRegions() {
        return ResponseEntity.ok(ApiResponse.success(vacancyService.getRegions()));
    }

    @GetMapping("/meta/districts")
    public ResponseEntity<ApiResponse<List<LookupDto>>> getDistricts(@RequestParam("regionId") Long regionId) {
        return ResponseEntity.ok(ApiResponse.success(vacancyService.getDistricts(regionId)));
    }
}
