package tz.co.halotel.halokazi.vacancy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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

@Tag(name = "1. Vacancies & Search", description = "Endpoints for browsing, searching, filtering published vacancies, and lookup metadata")
@RestController
@RequestMapping("/api/v1/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @Operation(
            summary = "Search and filter vacancies",
            description = "Public endpoint to search and filter published vacancies with pagination. Optional headers X-User-Id / X-Profile-Id will populate whether the vacancy has already been saved or applied to by the user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vacancies retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VacancySummaryDto>>> searchVacancies(
            @ParameterObject VacancyFilterParams params,
            @CurrentUser(required = false) UserPrincipal currentUser) {

        Long profileId = currentUser != null ? currentUser.getProfileId() : null;
        PageResponse<VacancySummaryDto> result = vacancyService.searchVacancies(params, profileId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(
            summary = "Get vacancy details by ID",
            description = "Public endpoint to retrieve complete details for a single published vacancy, including company profile, requirements, skills, and application/saved status."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vacancy details found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vacancy not found or expired")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VacancyDetailDto>> getVacancyById(
            @Parameter(description = "ID of the vacancy to retrieve", required = true, example = "1")
            @PathVariable("id") Long id,
            @CurrentUser(required = false) UserPrincipal currentUser) {

        Long profileId = currentUser != null ? currentUser.getProfileId() : null;
        VacancyDetailDto dto = vacancyService.getVacancyDetails(id, profileId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "Get job categories", description = "Lookup list of all active job categories (e.g., IT, Engineering, Healthcare).")
    @GetMapping("/meta/categories")
    public ResponseEntity<ApiResponse<List<LookupDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(vacancyService.getCategories()));
    }

    @Operation(summary = "Get employment types", description = "Lookup list of employment types (Full-Time, Part-Time, Contract, Internship, etc.).")
    @GetMapping("/meta/employment-types")
    public ResponseEntity<ApiResponse<List<LookupDto>>> getEmploymentTypes() {
        return ResponseEntity.ok(ApiResponse.success(vacancyService.getEmploymentTypes()));
    }

    @Operation(summary = "Get regions", description = "Lookup list of Tanzanian regions (Mainland & Zanzibar).")
    @GetMapping("/meta/regions")
    public ResponseEntity<ApiResponse<List<LookupDto>>> getRegions() {
        return ResponseEntity.ok(ApiResponse.success(vacancyService.getRegions()));
    }

    @Operation(summary = "Get districts for a region", description = "Lookup list of districts filtered by parent region ID.")
    @GetMapping("/meta/districts")
    public ResponseEntity<ApiResponse<List<LookupDto>>> getDistricts(
            @Parameter(description = "Parent Region ID", required = true, example = "7")
            @RequestParam("regionId") Long regionId) {
        return ResponseEntity.ok(ApiResponse.success(vacancyService.getDistricts(regionId)));
    }
}
