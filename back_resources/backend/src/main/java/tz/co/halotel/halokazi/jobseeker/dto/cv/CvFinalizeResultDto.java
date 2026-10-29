package tz.co.halotel.halokazi.jobseeker.dto.cv;

import java.util.List;

/**
 * Response for the finalize endpoint.
 * If valid, contains the finalized CV summary.
 * If invalid, contains ALL validation errors at once.
 */
public class CvFinalizeResultDto {

    private boolean valid;
    private CvSummaryDto cv;
    private List<CvValidationErrorDto> errors;

    public CvFinalizeResultDto() {}

    public static CvFinalizeResultDto success(CvSummaryDto cv) {
        CvFinalizeResultDto result = new CvFinalizeResultDto();
        result.valid = true;
        result.cv = cv;
        result.errors = List.of();
        return result;
    }

    public static CvFinalizeResultDto failure(List<CvValidationErrorDto> errors) {
        CvFinalizeResultDto result = new CvFinalizeResultDto();
        result.valid = false;
        result.errors = errors;
        return result;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public CvSummaryDto getCv() { return cv; }
    public void setCv(CvSummaryDto cv) { this.cv = cv; }
    public List<CvValidationErrorDto> getErrors() { return errors; }
    public void setErrors(List<CvValidationErrorDto> errors) { this.errors = errors; }
}
