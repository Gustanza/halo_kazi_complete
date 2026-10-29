package tz.co.halotel.halokazi.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.alert.dto.NotificationDto;
import tz.co.halotel.halokazi.alert.service.NotificationService;
import tz.co.halotel.halokazi.common.api.ApiResponse;
import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.session.AuthenticatedUser;
import tz.co.halotel.halokazi.session.CurrentUser;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationDto>>> getNotifications(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @CurrentUser AuthenticatedUser currentUser) {

        PageResponse<NotificationDto> notifications = notificationService.getNotifications(currentUser.userId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@CurrentUser AuthenticatedUser currentUser) {
        long count = notificationService.getUnreadCount(currentUser.userId());
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAsRead(
            @PathVariable("id") Long id,
            @CurrentUser AuthenticatedUser currentUser) {

        notificationService.markAsRead(id, currentUser.userId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", Collections.singletonMap("read", true)));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAllAsRead(@CurrentUser AuthenticatedUser currentUser) {
        notificationService.markAllAsRead(currentUser.userId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", Collections.singletonMap("readAll", true)));
    }
}
