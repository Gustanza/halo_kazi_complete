package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * Request to change a CV's template.
 */
public class CvTemplateUpdateRequest {

    @NotBlank(message = "Template code is required")
    @Size(max = 40, message = "Template code must be at most 40 characters")
    @Pattern(regexp = "(?i)CLASSIC|MODERN|PROFESSIONAL|MINIMAL|GRADUATE", message = "Template code is invalid")
    private String templateCode;

    public CvTemplateUpdateRequest() {}

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode != null ? templateCode.trim().toUpperCase() : null;
    }
}
