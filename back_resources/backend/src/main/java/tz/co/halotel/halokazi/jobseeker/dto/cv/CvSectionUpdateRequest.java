package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Section visibility, order and optional display-title update. */
public class CvSectionUpdateRequest {
    @NotBlank
    private String sectionCode;

    private Boolean visible;

    @Min(1)
    private Integer displayOrder;

    @Size(max = 100)
    private String customTitle;

    public CvSectionUpdateRequest() {}

    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode == null ? null : sectionCode.trim().toUpperCase(); }
    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public String getCustomTitle() { return customTitle; }
    public void setCustomTitle(String customTitle) { this.customTitle = customTitle == null ? null : customTitle.trim(); }
}
