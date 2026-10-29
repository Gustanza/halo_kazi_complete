package tz.co.halotel.halokazi.jobseeker.model;

/**
 * Tracks the last step completed in the CV Builder wizard.
 * Used to resume editing where the jobseeker left off.
 * Tracked in the in-memory DataStore only.
 */
public enum CvLastStep {
    PERSONAL_INFO,
    EXPERIENCE,
    EDUCATION,
    SKILLS,
    CERTIFICATIONS,
    LANGUAGES,
    PROJECTS,
    REFERENCES,
    TEMPLATE
}
