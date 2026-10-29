package tz.co.halotel.halokazi.jobseeker;

import java.time.OffsetDateTime;

import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JOB_SEEKER_PROFILE - what registration and the Settings screen need (preference columns come from
 * 05_settings.sql). Other columns keep their database defaults.
 */
@Entity
@Table(name = "job_seeker_profile")
public class JobSeekerProfile {

    /** Notification choices. Job alerts map to the original receive_job_alerts column. */
    public record Notifications(boolean email, boolean applicationUpdates, boolean jobAlerts, boolean newsletter) {
    }

    /** What employers may see. */
    public record Privacy(boolean profileVisible, boolean showEmail, boolean showPhone) {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    // Java defaults mirror the column defaults, because Hibernate inserts every mapped column.
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "notify_email", nullable = false)
    private boolean notifyEmail = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "notify_application_updates", nullable = false)
    private boolean notifyApplicationUpdates = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "receive_job_alerts", nullable = false)
    private boolean receiveJobAlerts = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "notify_newsletter", nullable = false)
    private boolean notifyNewsletter = false;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "profile_visible", nullable = false)
    private boolean profileVisible = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "show_email", nullable = false)
    private boolean showEmail = false;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "show_phone", nullable = false)
    private boolean showPhone = false;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected JobSeekerProfile() {
    }

    public static JobSeekerProfile emptyFor(Long userId) {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.userId = userId;
        return profile;
    }

    public void updateNotifications(Notifications choice) {
        notifyEmail = choice.email();
        notifyApplicationUpdates = choice.applicationUpdates();
        receiveJobAlerts = choice.jobAlerts();
        notifyNewsletter = choice.newsletter();
    }

    public void updatePrivacy(Privacy choice) {
        profileVisible = choice.profileVisible();
        showEmail = choice.showEmail();
        showPhone = choice.showPhone();
    }

    public Notifications notifications() {
        return new Notifications(notifyEmail, notifyApplicationUpdates, receiveJobAlerts, notifyNewsletter);
    }

    public Privacy privacy() {
        return new Privacy(profileVisible, showEmail, showPhone);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
}
