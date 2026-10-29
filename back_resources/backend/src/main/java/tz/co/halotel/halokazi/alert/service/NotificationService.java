package tz.co.halotel.halokazi.alert.service;

import tz.co.halotel.halokazi.alert.dto.NotificationDto;
import tz.co.halotel.halokazi.common.api.PageResponse;

public interface NotificationService {

    PageResponse<NotificationDto> getNotifications(Long userId, int page, int size);

    long getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);
}
