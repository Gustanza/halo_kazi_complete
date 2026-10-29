package tz.co.halotel.halokazi.jobseeker.dto;

import jakarta.validation.constraints.Size;

public class HeadlineSummaryUpdateRequest {
    @Size(max = 150, message = "Headline cannot exceed 150 characters")
    private String headline;

    private String professionalSummary;

    public HeadlineSummaryUpdateRequest() {}

    public HeadlineSummaryUpdateRequest(String headline, String professionalSummary) {
        this.headline = headline;
        this.professionalSummary = professionalSummary;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }
}
