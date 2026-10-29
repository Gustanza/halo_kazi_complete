package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Single reference entry for PUT /{cvId}/references.
 */
public class CvReferenceItemRequest {

    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must be at most 150 characters")
    private String fullName;

    @Size(max = 150, message = "Job title must be at most 150 characters")
    private String jobTitle;

    @Size(max = 200, message = "Organization must be at most 200 characters")
    private String organization;

    @Size(max = 100, message = "Relationship must be at most 100 characters")
    private String relationship;

    @Email(message = "Email must be a valid email address")
    @Size(max = 254, message = "Email must be at most 254 characters")
    private String email;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    private int sortOrder;

    public CvReferenceItemRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName != null ? fullName.trim() : null; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle != null ? jobTitle.trim() : null; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization != null ? organization.trim() : null; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship != null ? relationship.trim() : null; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email.trim() : null; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone != null ? phone.trim() : null; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
