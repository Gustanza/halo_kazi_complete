package tz.co.halotel.halokazi.jobseeker.dto;

public class JobSeekerDashboardStatsDto {
    private int applicationsCount;
    private int savedJobsCount;
    private int activeJobAlertsCount;
    private int cvsCreatedCount;

    public JobSeekerDashboardStatsDto() {}

    public JobSeekerDashboardStatsDto(int applicationsCount, int savedJobsCount, int activeJobAlertsCount, int cvsCreatedCount) {
        this.applicationsCount = applicationsCount;
        this.savedJobsCount = savedJobsCount;
        this.activeJobAlertsCount = activeJobAlertsCount;
        this.cvsCreatedCount = cvsCreatedCount;
    }

    public int getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(int applicationsCount) {
        this.applicationsCount = applicationsCount;
    }

    public int getSavedJobsCount() {
        return savedJobsCount;
    }

    public void setSavedJobsCount(int savedJobsCount) {
        this.savedJobsCount = savedJobsCount;
    }

    public int getActiveJobAlertsCount() {
        return activeJobAlertsCount;
    }

    public void setActiveJobAlertsCount(int activeJobAlertsCount) {
        this.activeJobAlertsCount = activeJobAlertsCount;
    }

    public int getCvsCreatedCount() {
        return cvsCreatedCount;
    }

    public void setCvsCreatedCount(int cvsCreatedCount) {
        this.cvsCreatedCount = cvsCreatedCount;
    }
}
