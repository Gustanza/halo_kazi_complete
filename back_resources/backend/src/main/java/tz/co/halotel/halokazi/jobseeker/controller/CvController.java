package tz.co.halotel.halokazi.jobseeker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.dto.cv.*;
import tz.co.halotel.halokazi.jobseeker.model.Cv;
import tz.co.halotel.halokazi.jobseeker.service.CvPdfRenderer;
import tz.co.halotel.halokazi.jobseeker.service.CvService;

import java.nio.charset.StandardCharsets;
import java.util.List;

/** HTTP API for CV Builder and My CVs. Mutating requests require the CV version in If-Match. */
@RestController
@Validated
@RequestMapping("/api/v1/jobseeker/cvs")
public class CvController {

    private final CvService cvService;
    private final CvPdfRenderer pdfRenderer;

    public CvController(CvService cvService, CvPdfRenderer pdfRenderer) {
        this.cvService = cvService;
        this.pdfRenderer = pdfRenderer;
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<CvTemplateDto>>> templates() {
        return ResponseEntity.ok(ApiResponse.success(cvService.getTemplates()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CvDetailDto>> createDraft(@Valid @RequestBody(required = false) CreateCvDraftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("CV draft created", cvService.createDraft(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CvPageDto>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        return ResponseEntity.ok(ApiResponse.success(cvService.listMyCvs(page, size)));
    }

    @GetMapping("/{cvId}")
    public ResponseEntity<ApiResponse<CvDetailDto>> get(@PathVariable Long cvId) {
        return ResponseEntity.ok(ApiResponse.success(cvService.getCv(cvId)));
    }

    @PutMapping("/{cvId}/personal-info")
    public ResponseEntity<ApiResponse<CvDetailDto>> savePersonalInfo(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @Valid @RequestBody CvPersonalInfoRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cvService.savePersonalInfo(cvId, expectedVersion(ifMatch), request)));
    }

    @PutMapping("/{cvId}/experience")
    public ResponseEntity<ApiResponse<CvDetailDto>> saveExperiences(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody @NotNull @Size(max = 100) List<@NotNull @Valid CvExperienceItemRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(cvService.saveExperiences(cvId, expectedVersion(ifMatch), requests)));
    }

    @PutMapping("/{cvId}/education")
    public ResponseEntity<ApiResponse<CvDetailDto>> saveEducations(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody @NotNull @Size(max = 100) List<@NotNull @Valid CvEducationItemRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(cvService.saveEducations(cvId, expectedVersion(ifMatch), requests)));
    }

    @PutMapping("/{cvId}/skills")
    public ResponseEntity<ApiResponse<CvDetailDto>> saveSkills(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody @NotNull @Size(max = 100) List<@NotNull @Valid CvSkillItemRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(cvService.saveSkills(cvId, expectedVersion(ifMatch), requests)));
    }

    @PutMapping("/{cvId}/certifications")
    public ResponseEntity<ApiResponse<CvDetailDto>> saveCertifications(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody @NotNull @Size(max = 100) List<@NotNull @Valid CvCertificationItemRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(cvService.saveCertifications(cvId, expectedVersion(ifMatch), requests)));
    }

    @PutMapping("/{cvId}/languages")
    public ResponseEntity<ApiResponse<CvDetailDto>> saveLanguages(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody @NotNull @Size(max = 100) List<@NotNull @Valid CvLanguageItemRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(cvService.saveLanguages(cvId, expectedVersion(ifMatch), requests)));
    }

    @PutMapping("/{cvId}/projects")
    public ResponseEntity<ApiResponse<CvDetailDto>> saveProjects(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody @NotNull @Size(max = 100) List<@NotNull @Valid CvProjectItemRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(cvService.saveProjects(cvId, expectedVersion(ifMatch), requests)));
    }

    @PutMapping("/{cvId}/references")
    public ResponseEntity<ApiResponse<CvDetailDto>> saveReferences(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody @NotNull @Size(max = 100) List<@NotNull @Valid CvReferenceItemRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(cvService.saveReferences(cvId, expectedVersion(ifMatch), requests)));
    }

    @PutMapping("/{cvId}/sections")
    public ResponseEntity<ApiResponse<CvDetailDto>> saveSectionConfiguration(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody @NotNull @Size(max = 20) List<@NotNull @Valid CvSectionUpdateRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(cvService.saveSectionConfiguration(cvId, expectedVersion(ifMatch), requests)));
    }

    @PutMapping("/{cvId}/template")
    public ResponseEntity<ApiResponse<CvDetailDto>> chooseTemplate(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @Valid @RequestBody CvTemplateUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cvService.chooseTemplate(cvId, expectedVersion(ifMatch), request.getTemplateCode())));
    }

    @PostMapping("/{cvId}/finalize")
    public ResponseEntity<ApiResponse<CvFinalizeResultDto>> finalizeCv(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch) {
        CvFinalizeResultDto result = cvService.finalizeCv(cvId, expectedVersion(ifMatch));
        if (!result.isValid()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ApiResponse<>(false, "CV is incomplete", result));
        }
        return ResponseEntity.ok(ApiResponse.success("CV finalized", result));
    }

    @PutMapping("/{cvId}/title")
    public ResponseEntity<ApiResponse<CvDetailDto>> rename(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @Valid @RequestBody CvTitleUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cvService.rename(cvId, expectedVersion(ifMatch), request)));
    }

    @PostMapping("/{cvId}/duplicate")
    public ResponseEntity<ApiResponse<CvDetailDto>> duplicate(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("CV duplicated", cvService.duplicate(cvId, expectedVersion(ifMatch))));
    }

    @PutMapping("/{cvId}/default")
    public ResponseEntity<ApiResponse<CvDetailDto>> setDefault(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch) {
        return ResponseEntity.ok(ApiResponse.success("Default CV updated", cvService.setDefault(cvId, expectedVersion(ifMatch))));
    }

    @DeleteMapping("/{cvId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long cvId, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch) {
        cvService.delete(cvId, expectedVersion(ifMatch));
        return ResponseEntity.ok(ApiResponse.success("CV deleted", null));
    }

    @GetMapping(value = "/{cvId}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable Long cvId) {
        Cv cv = cvService.getForDownload(cvId);
        byte[] pdf = pdfRenderer.render(cv);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("cv-" + cv.getCvId() + ".pdf", StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }

    private int expectedVersion(String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) {
            throw new IllegalArgumentException("If-Match header is required; send the CV version");
        }
        String value = ifMatch.trim();
        if (value.startsWith("W/")) value = value.substring(2).trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        try {
            int version = Integer.parseInt(value);
            if (version < 1) throw new NumberFormatException();
            return version;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("If-Match must contain the numeric CV version");
        }
    }
}
