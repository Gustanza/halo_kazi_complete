package tz.co.halotel.halokazi.jobseeker.model;

public class ProfileLanguage {
    private Long id;
    private Long profileId;
    private Long languageId;
    private String languageName;
    private String isoCode;
    private String proficiency; // BASIC | CONVERSATIONAL | FLUENT | NATIVE

    public ProfileLanguage() {}

    public ProfileLanguage(Long id, Long profileId, Long languageId, String languageName, String isoCode, String proficiency) {
        this.id = id;
        this.profileId = profileId;
        this.languageId = languageId;
        this.languageName = languageName;
        this.isoCode = isoCode;
        this.proficiency = proficiency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }
}
