package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * Request to create a new CV draft, prefilled from the jobseeker's profile.
 * All fields optional — the system auto-generates defaults.
 */
public class CreateCvDraftRequest {

    @Size(max = 100, message = "CV title must be at most 100 characters")
    private String title;

    @Size(max = 40, message = "Template code must be at most 40 characters")
    @Pattern(regexp = "(?i)CLASSIC|MODERN|PROFESSIONAL|MINIMAL|GRADUATE", message = "Template code is invalid")
    private String templateCode;

    public CreateCvDraftRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode != null ? templateCode.trim().toUpperCase() : null;
    }
}
