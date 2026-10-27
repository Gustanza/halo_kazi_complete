package tz.co.halotel.halokazi.settings;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Read-only view of SYSTEM_SETTING; admins maintain the values. */
@Entity
@Immutable
@Table(name = "system_setting")
public class SystemSetting {

    @Id
    @Column(name = "setting_key")
    private String key;

    @Column(name = "setting_value", nullable = false)
    private String value;

    protected SystemSetting() {
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
