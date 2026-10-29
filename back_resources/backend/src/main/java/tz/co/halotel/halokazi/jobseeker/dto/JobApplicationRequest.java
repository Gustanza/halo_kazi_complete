package tz.co.halotel.halokazi.jobseeker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JobApplicationRequest {
    @NotNull(message = "Vacancy ID is required")
    private Long vacancyId;

    private Long cvId;

    @NotBlank(message = "Cover letter is required")
    private String coverLetter;

    private boolean requirementsConfirmed = true;

    public JobApplicationRequest() {}

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

    public boolean isRequirementsConfirmed() {
        return requirementsConfirmed;
    }

    public void setRequirementsConfirmed(boolean requirementsConfirmed) {
        this.requirementsConfirmed = requirementsConfirmed;
    }
}
