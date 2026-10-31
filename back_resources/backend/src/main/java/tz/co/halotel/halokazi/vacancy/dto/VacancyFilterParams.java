package tz.co.halotel.halokazi.vacancy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Query parameters for searching and filtering published vacancies")
public class VacancyFilterParams {

    @Schema(description = "Keyword search matching job title, summary, description, or employer name", example = "Software")
    private String keyword;

    @Schema(description = "Filter by Job Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Filter by Region ID", example = "7")
    private Long regionId;

    @Schema(description = "Filter by Employment Type ID (e.g. 1 for Full-Time)", example = "1")
    private Long employmentTypeId;

    @Schema(description = "Minimum required years of experience", example = "2.0")
    private Double minExperienceYears;

    @Schema(description = "Page number (0-indexed)", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "Number of items per page", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "Sort criteria in field,direction format", example = "publishedAt,desc", defaultValue = "publishedAt,desc")
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
