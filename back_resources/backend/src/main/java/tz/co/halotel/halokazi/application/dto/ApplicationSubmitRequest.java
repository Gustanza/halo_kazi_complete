package tz.co.halotel.halokazi.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationSubmitRequest {

    @NotNull(message = "Vacancy ID is required")
    private Long vacancyId;

    private Long cvId;

    @NotBlank(message = "Cover letter is required")
    private String coverLetter;

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
