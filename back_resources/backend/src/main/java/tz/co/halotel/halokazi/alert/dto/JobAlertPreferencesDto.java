package tz.co.halotel.halokazi.alert.dto;

import java.util.ArrayList;
import java.util.List;

public class JobAlertPreferencesDto {

    private boolean receiveJobAlerts;
    private List<Long> categoryIds = new ArrayList<>();
    private List<Long> regionIds = new ArrayList<>();
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
