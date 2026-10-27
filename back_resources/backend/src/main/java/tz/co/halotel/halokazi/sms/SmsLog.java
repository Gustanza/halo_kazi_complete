package tz.co.halotel.halokazi.sms;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** SMS_LOG row. Never holds the message text, so OTP codes are not persisted. */
@Entity
@Table(name = "sms_log")
class SmsLog {

    private static final int MAX_ERROR_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "purpose", nullable = false)
    private String purpose;

    @Column(name = "provider_message_id")
    private String providerMessageId;

    @Column(name = "delivery_status", nullable = false)
    private String deliveryStatus;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    protected SmsLog() {
    }

    static SmsLog sent(Long userId, String phone, String purpose, String providerMessageId, OffsetDateTime sentAt) {
        SmsLog row = base(userId, phone, purpose, "SENT");
        row.providerMessageId = providerMessageId;
        row.sentAt = sentAt;
        return row;
    }

    static SmsLog failed(Long userId, String phone, String purpose, String error) {
        SmsLog row = base(userId, phone, purpose, "FAILED");
        row.errorMessage = error == null || error.length() <= MAX_ERROR_LENGTH ? error : error.substring(0, MAX_ERROR_LENGTH);
        return row;
    }

    private static SmsLog base(Long userId, String phone, String purpose, String status) {
        SmsLog row = new SmsLog();
        row.userId = userId;
        row.phone = phone;
        row.purpose = purpose;
        row.deliveryStatus = status;
        return row;
    }
}
