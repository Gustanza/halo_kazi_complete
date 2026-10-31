package tz.co.halotel.halokazi.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Job application submission payload")
public class ApplicationSubmitRequest {

    @Schema(description = "ID of the target vacancy to apply for", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Vacancy ID is required")
    private Long vacancyId;

    @Schema(description = "ID of the CV to attach. If null, the user's primary/default active CV is automatically used.", example = "1")
    private Long cvId;

    @Schema(description = "Personalized cover letter text explaining qualifications", example = "Dear Hiring Team,\n\nI am thrilled to apply for the Senior Java Developer position...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Cover letter is required")
    private String coverLetter;

    @Schema(description = "Confirmation that candidate has verified and meets mandatory requirements", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "You must confirm that you meet the job requirements")
    private Boolean requirementsConfirmed;

    public ApplicationSubmitRequest() {
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Long vacancyId) {
        this.vacancyId = vacancyId;
    }

    public Long getCvId() {
        return cvId;
    }

    public void setCvId(Long cvId) {
        this.cvId = cvId;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public Boolean getRequirementsConfirmed() {
        return requirementsConfirmed;
    }

    public void setRequirementsConfirmed(Boolean requirementsConfirmed) {
        this.requirementsConfirmed = requirementsConfirmed;
    }
}
