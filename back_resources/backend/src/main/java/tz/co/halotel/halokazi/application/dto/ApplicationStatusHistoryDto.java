package tz.co.halotel.halokazi.application.dto;

import java.time.OffsetDateTime;

public class ApplicationStatusHistoryDto {

    private String fromStatus;
    private String toStatus;
    private OffsetDateTime changedAt;
    private String remarks;

    public ApplicationStatusHistoryDto() {
    }

    public ApplicationStatusHistoryDto(String fromStatus, String toStatus, OffsetDateTime changedAt, String remarks) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedAt = changedAt;
        this.remarks = remarks;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public OffsetDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(OffsetDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
