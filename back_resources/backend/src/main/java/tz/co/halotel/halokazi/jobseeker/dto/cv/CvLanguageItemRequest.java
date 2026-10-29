package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Single language proficiency entry for PUT /{cvId}/languages.
 */
public class CvLanguageItemRequest {

    private Long id;

    @NotBlank(message = "Language name is required")
    @Size(max = 100, message = "Language name must be at most 100 characters")
    private String languageName;

    @Size(max = 10, message = "Language code must be at most 10 characters")
    private String languageCode;

    @Size(max = 20, message = "Proficiency must be at most 20 characters")
    private String proficiency;

    private int sortOrder;

    public CvLanguageItemRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName != null ? languageName.trim() : null; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode != null ? languageCode.trim().toLowerCase() : null; }

    public String getProficiency() { return proficiency; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency != null ? proficiency.trim().toUpperCase() : null; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
