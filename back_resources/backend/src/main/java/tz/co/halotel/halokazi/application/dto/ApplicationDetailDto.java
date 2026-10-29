package tz.co.halotel.halokazi.application.dto;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDetailDto extends ApplicationSummaryDto {

    private String coverLetter;
    private Boolean requirementsConfirmed;
    private String profileSnapshot;
    private String cvSnapshot;
    private Double profileCompletionScore;
    private String statusRemarks;
    private List<ApplicationStatusHistoryDto> statusHistory = new ArrayList<>();

    public ApplicationDetailDto() {
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

    public String getProfileSnapshot() {
        return profileSnapshot;
    }

    public void setProfileSnapshot(String profileSnapshot) {
        this.profileSnapshot = profileSnapshot;
    }

    public String getCvSnapshot() {
        return cvSnapshot;
    }

    public void setCvSnapshot(String cvSnapshot) {
        this.cvSnapshot = cvSnapshot;
    }

    public Double getProfileCompletionScore() {
        return profileCompletionScore;
    }

    public void setProfileCompletionScore(Double profileCompletionScore) {
        this.profileCompletionScore = profileCompletionScore;
    }

    public String getStatusRemarks() {
        return statusRemarks;
    }

    public void setStatusRemarks(String statusRemarks) {
        this.statusRemarks = statusRemarks;
    }

    public List<ApplicationStatusHistoryDto> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<ApplicationStatusHistoryDto> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
