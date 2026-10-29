package tz.co.halotel.halokazi.jobseeker.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Maps to Oracle Table: cv_template
 * (5 rows pre-seeded: CLASSIC, MODERN, PROFESSIONAL, MINIMAL, GRADUATE)
 */
@Entity
@Table(name = "cv_template")
public class CvTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "code", nullable = false, unique = true, length = 40)
    private String code;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "name_sw", nullable = false, length = 100)
    private String nameSw;

    @Column(name = "description_en", length = 500)
    private String descriptionEn;

    @Column(name = "description_sw", length = 500)
    private String descriptionSw;

    @Lob
    @Column(name = "layout_definition")
    private String layoutDefinition;

    @Column(name = "preview_file_id")
    private Long previewFileId;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public CvTemplateEntity() {}

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameSw() {
        return nameSw;
    }

    public void setNameSw(String nameSw) {
        this.nameSw = nameSw;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionSw() {
        return descriptionSw;
    }

    public void setDescriptionSw(String descriptionSw) {
        this.descriptionSw = descriptionSw;
    }

    public String getLayoutDefinition() {
        return layoutDefinition;
    }

    public void setLayoutDefinition(String layoutDefinition) {
        this.layoutDefinition = layoutDefinition;
    }

    public Long getPreviewFileId() {
        return previewFileId;
    }

    public void setPreviewFileId(Long previewFileId) {
        this.previewFileId = previewFileId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
