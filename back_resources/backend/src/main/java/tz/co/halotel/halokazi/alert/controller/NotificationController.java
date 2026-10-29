package tz.co.halotel.halokazi.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.alert.dto.NotificationDto;
import tz.co.halotel.halokazi.alert.service.NotificationService;
import tz.co.halotel.halokazi.common.api.ApiResponse;
import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.common.security.CurrentUser;
import tz.co.halotel.halokazi.common.security.UserPrincipal;

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
            @CurrentUser UserPrincipal currentUser) {

        PageResponse<NotificationDto> notifications = notificationService.getNotifications(currentUser.getUserId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@CurrentUser UserPrincipal currentUser) {
        long count = notificationService.getUnreadCount(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAsRead(
            @PathVariable("id") Long id,
            @CurrentUser UserPrincipal currentUser) {

        notificationService.markAsRead(id, currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", Collections.singletonMap("read", true)));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAllAsRead(@CurrentUser UserPrincipal currentUser) {
        notificationService.markAllAsRead(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", Collections.singletonMap("readAll", true)));
    }
}
