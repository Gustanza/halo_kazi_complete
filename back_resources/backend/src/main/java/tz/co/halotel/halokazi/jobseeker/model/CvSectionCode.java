package tz.co.halotel.halokazi.jobseeker.model;

/**
 * Matches the Oracle CHECK constraint on cv_section.section_code:
 * ('CONTACT', 'SUMMARY', 'WORK_EXPERIENCE', 'EDUCATION', 'SKILLS',
 *  'CERTIFICATIONS', 'LANGUAGES', 'PROJECTS', 'ACHIEVEMENTS',
 *  'REFERENCES', 'INTERESTS')
 */
public enum CvSectionCode {
    CONTACT("Contact Information"),
    SUMMARY("Professional Summary"),
    WORK_EXPERIENCE("Work Experience"),
    EDUCATION("Education"),
    SKILLS("Skills"),
    CERTIFICATIONS("Certifications"),
    LANGUAGES("Languages"),
    PROJECTS("Projects"),
    ACHIEVEMENTS("Achievements"),
    REFERENCES("References"),
    INTERESTS("Interests");

    private final String defaultTitle;

    CvSectionCode(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }
}
