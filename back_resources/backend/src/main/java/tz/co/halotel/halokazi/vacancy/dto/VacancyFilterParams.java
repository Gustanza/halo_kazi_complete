package tz.co.halotel.halokazi.vacancy.dto;

public class VacancyFilterParams {

    private String keyword;
    private Long categoryId;
    private Long regionId;
    private Long employmentTypeId;
    private Double minExperienceYears;
    private Integer page = 0;
    private Integer size = 10;
    private String sort = "publishedAt,desc";

    public VacancyFilterParams() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public Double getMinExperienceYears() {
        return minExperienceYears;
    }

    public void setMinExperienceYears(Double minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }

    public Integer getPage() {
        return page != null && page >= 0 ? page : 0;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size != null && size > 0 && size <= 100 ? size : 10;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSort() {
        return sort != null && !sort.isBlank() ? sort : "publishedAt,desc";
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
