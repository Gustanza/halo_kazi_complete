package tz.co.halotel.halokazi.auth.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import tz.co.halotel.halokazi.user.Role;

/**
 * Roles a person may self-register with. Case-insensitive on input so the UI's
 * "job_seeker" / "employer" and "JOB_SEEKER" / "EMPLOYER" are both accepted.
 */
public enum RegistrationRole {
    JOB_SEEKER(Role.JOB_SEEKER),
    EMPLOYER(Role.EMPLOYER);

    private final Role role;

    RegistrationRole(Role role) {
        this.role = role;
    }

    /** Unknown values fail deserialization and are reported as a field error on "role". */
    @JsonCreator
    public static RegistrationRole fromJson(String value) {
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    public Role toRole() {
        return role;
    }
}
