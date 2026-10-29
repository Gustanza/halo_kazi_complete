package tz.co.halotel.halokazi.jobseeker.dto.cv;

/**
 * Template information exposed to the frontend.
 */
public class CvTemplateDto {

    private Long templateId;
    private String code;
    private String nameEn;
    private String nameSw;
    private String descriptionEn;
    private String descriptionSw;
    private Integer sortOrder;

    public CvTemplateDto() {}

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getNameSw() { return nameSw; }
    public void setNameSw(String nameSw) { this.nameSw = nameSw; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getDescriptionSw() { return descriptionSw; }
    public void setDescriptionSw(String descriptionSw) { this.descriptionSw = descriptionSw; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
