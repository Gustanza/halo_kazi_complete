package tz.co.halotel.halokazi.jobseeker.model;

import java.time.LocalDateTime;

/** Lightweight metadata-only row used by the My CVs list. */
public record CvSummaryProjection(Long cvId, String cvName, String templateCode, String templateName,
                                  CvStatus status, boolean isDefault, String cvLanguage,
                                  LocalDateTime updatedAt, LocalDateTime createdAt) {}
