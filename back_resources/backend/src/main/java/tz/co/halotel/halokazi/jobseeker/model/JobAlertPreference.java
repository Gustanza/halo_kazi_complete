package tz.co.halotel.halokazi.jobseeker.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JobAlertPreference {
    private Long profileId;
    private boolean enabled;
    private boolean receiveEmailAlerts;
    private boolean receiveSmsAlerts;
    private String frequency; // DAILY, WEEKLY, INSTANT
    private List<String> keywords = new ArrayList<>();
    private List<String> preferredLocations = new ArrayList<>();
    private List<String> preferredCategories = new ArrayList<>();
    private BigDecimal minSalary;

    public JobAlertPreference() {}

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isReceiveEmailAlerts() {
        return receiveEmailAlerts;
    }

    public void setReceiveEmailAlerts(boolean receiveEmailAlerts) {
        this.receiveEmailAlerts = receiveEmailAlerts;
    }

    public boolean isReceiveSmsAlerts() {
        return receiveSmsAlerts;
    }

    public void setReceiveSmsAlerts(boolean receiveSmsAlerts) {
        this.receiveSmsAlerts = receiveSmsAlerts;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getPreferredLocations() {
        return preferredLocations;
    }

    public void setPreferredLocations(List<String> preferredLocations) {
        this.preferredLocations = preferredLocations;
    }

    public List<String> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(List<String> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }
}
