package tz.co.halotel.halokazi.jobseeker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ProfileVisibilityUpdateRequest {
    @NotBlank(message = "Visibility is required")
    @Pattern(regexp = "Public|Private|PUBLIC|PRIVATE", message = "Visibility must be Public or Private")
    private String visibility;

    public ProfileVisibilityUpdateRequest() {}

    public ProfileVisibilityUpdateRequest(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
