package tz.co.halotel.halokazi.jobseeker.model;

public class RecommendedJob {
    private Long id;
    private String title;
    private String company;
    private String initials;
    private String brandColor;
    private String location;
    private String type; // Full Time, Part Time, Contract
    private String salary;
    private int matchPercentage;
    private boolean isNew;

    public RecommendedJob() {}

    public RecommendedJob(Long id, String title, String company, String initials, String brandColor, String location, String type, String salary, int matchPercentage, boolean isNew) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.initials = initials;
        this.brandColor = brandColor;
        this.location = location;
        this.type = type;
        this.salary = salary;
        this.matchPercentage = matchPercentage;
        this.isNew = isNew;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getBrandColor() {
        return brandColor;
    }

    public void setBrandColor(String brandColor) {
        this.brandColor = brandColor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
