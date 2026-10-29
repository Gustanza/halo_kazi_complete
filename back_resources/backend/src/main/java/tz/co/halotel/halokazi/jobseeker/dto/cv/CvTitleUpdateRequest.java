package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request to rename a CV.
 */
public class CvTitleUpdateRequest {

    @NotBlank(message = "CV title is required")
    @Size(max = 100, message = "CV title must be at most 100 characters")
    private String title;

    public CvTitleUpdateRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }
}
