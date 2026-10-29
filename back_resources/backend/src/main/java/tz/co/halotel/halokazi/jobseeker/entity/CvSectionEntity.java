package tz.co.halotel.halokazi.jobseeker.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Maps to Oracle Table: cv_section
 * Composite Primary Key: cv_id, section_code
 * Controls which sections are visible on a CV and their display order.
 */
@Entity
@Table(name = "cv_section")
@IdClass(CvSectionEntity.CvSectionId.class)
public class CvSectionEntity {

    @Id
    @Column(name = "cv_id", nullable = false)
    private Long cvId;

    @Id
    @Column(name = "section_code", nullable = false, length = 30)
    private String sectionCode;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "custom_title", length = 100)
    private String customTitle;

    public CvSectionEntity() {}

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

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public static class CvSectionId implements Serializable {
        private Long cvId;
        private String sectionCode;

        public CvSectionId() {}

        public CvSectionId(Long cvId, String sectionCode) {
            this.cvId = cvId;
            this.sectionCode = sectionCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CvSectionId that = (CvSectionId) o;
            return Objects.equals(cvId, that.cvId) && Objects.equals(sectionCode, that.sectionCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cvId, sectionCode);
        }
    }
}
