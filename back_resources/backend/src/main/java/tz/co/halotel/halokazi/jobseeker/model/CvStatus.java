package tz.co.halotel.halokazi.jobseeker.model;

/**
 * CV lifecycle status.
 * Note: This field is tracked in the in-memory DataStore only;
 * the Oracle CV table does not yet have a status column.
 */
public enum CvStatus {
    DRAFT,
    COMPLETED
}
