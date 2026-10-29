package tz.co.halotel.halokazi.jobseeker.dto.cv;

import java.time.LocalDateTime;

/**
 * Lightweight CV summary for list endpoints (My CVs).
 * Does NOT include section data — only metadata.
 */
public class CvSummaryDto {

    private Long cvId;
    private String cvName;
    private String templateCode;
    private String templateName;
    private String status;
    private boolean isDefault;
    private String cvLanguage;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public CvSummaryDto() {}

    public Long getCvId() { return cvId; }
    public void setCvId(Long cvId) { this.cvId = cvId; }
    public String getCvName() { return cvName; }
    public void setCvName(String cvName) { this.cvName = cvName; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public String getCvLanguage() { return cvLanguage; }
    public void setCvLanguage(String cvLanguage) { this.cvLanguage = cvLanguage; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
