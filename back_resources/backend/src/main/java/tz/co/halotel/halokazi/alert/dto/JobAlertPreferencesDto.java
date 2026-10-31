package tz.co.halotel.halokazi.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Job alert subscription preferences")
public class JobAlertPreferencesDto {

    @Schema(description = "Whether to receive automated job alert notifications", example = "true")
    private boolean receiveJobAlerts;

    @Schema(description = "List of preferred Job Category IDs", example = "[1, 2]")
    private List<Long> categoryIds = new ArrayList<>();

    @Schema(description = "List of preferred Region IDs", example = "[7]")
    private List<Long> regionIds = new ArrayList<>();

    @Schema(description = "List of preferred Employment Type IDs", example = "[1]")
    private List<Long> employmentTypeIds = new ArrayList<>();

    public JobAlertPreferencesDto() {
    }

    public JobAlertPreferencesDto(boolean receiveJobAlerts, List<Long> categoryIds, List<Long> regionIds, List<Long> employmentTypeIds) {
        this.receiveJobAlerts = receiveJobAlerts;
        this.categoryIds = categoryIds != null ? categoryIds : new ArrayList<>();
        this.regionIds = regionIds != null ? regionIds : new ArrayList<>();
        this.employmentTypeIds = employmentTypeIds != null ? employmentTypeIds : new ArrayList<>();
    }

    public boolean isReceiveJobAlerts() {
        return receiveJobAlerts;
    }

    public void setReceiveJobAlerts(boolean receiveJobAlerts) {
        this.receiveJobAlerts = receiveJobAlerts;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Long> getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(List<Long> regionIds) {
        this.regionIds = regionIds;
    }

    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds;
    }

    public void setEmploymentTypeIds(List<Long> employmentTypeIds) {
        this.employmentTypeIds = employmentTypeIds;
    }
}
