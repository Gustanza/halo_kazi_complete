package tz.co.halotel.halokazi.jobseeker.model;

/**
 * In-memory representation of a CV section configuration.
 * Controls which sections are visible, their order, and custom titles.
 */
public class CvSection {

    private Long cvId;
    private String sectionCode;
    private boolean visible;
    private int displayOrder;
    private String customTitle;

    public CvSection() {}

    public CvSection(Long cvId, String sectionCode, boolean visible, int displayOrder, String customTitle) {
        this.cvId = cvId;
        this.sectionCode = sectionCode;
        this.visible = visible;
        this.displayOrder = displayOrder;
        this.customTitle = customTitle;
    }

    public Long getCvId() {
        return cvId;
    }

    public void setCvId(Long cvId) {
        this.cvId = cvId;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }
}
