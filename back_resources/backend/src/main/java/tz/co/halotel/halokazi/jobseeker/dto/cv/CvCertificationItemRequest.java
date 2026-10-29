package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Single certification entry for PUT /{cvId}/certifications.
 */
public class CvCertificationItemRequest {

    private Long id;

    @NotBlank(message = "Certification name is required")
    @Size(max = 200, message = "Certification name must be at most 200 characters")
    private String certificationName;

    @NotBlank(message = "Issuing organization is required")
    @Size(max = 200, message = "Issuing organization must be at most 200 characters")
    private String issuingOrganization;

    @Size(max = 100, message = "Credential ID must be at most 100 characters")
    private String credentialId;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    @Size(max = 500, message = "Certificate URL must be at most 500 characters")
    private String certificateUrl;

    private int sortOrder;

    public CvCertificationItemRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertificationName() { return certificationName; }
    public void setCertificationName(String certificationName) { this.certificationName = certificationName != null ? certificationName.trim() : null; }

    public String getIssuingOrganization() { return issuingOrganization; }
    public void setIssuingOrganization(String issuingOrganization) { this.issuingOrganization = issuingOrganization != null ? issuingOrganization.trim() : null; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId != null ? credentialId.trim() : null; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl != null ? certificateUrl.trim() : null; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
