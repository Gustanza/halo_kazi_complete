package tz.co.halotel.halokazi.jobseeker.dto.cv;

/**
 * Structured validation error returned in the finalize response.
 * Each error identifies the section, the field, and a human-readable message.
 */
public class CvValidationErrorDto {

    private String section;
    private String field;
    private String message;

    public CvValidationErrorDto() {}

    public CvValidationErrorDto(String section, String field, String message) {
        this.section = section;
        this.field = field;
        this.message = message;
    }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
