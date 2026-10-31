package tz.co.halotel.halokazi.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "5. Notifications", description = "Endpoints for managing user in-app notifications and unread counters")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "Get user notifications",
            description = "Retrieves paginated list of in-app notifications (application status updates, job alert matches, reminders) for the authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationDto>>> getNotifications(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @CurrentUser UserPrincipal currentUser) {

        PageResponse<NotificationDto> notifications = notificationService.getNotifications(currentUser.getUserId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @Operation(
            summary = "Get unread notifications count",
            description = "Returns the total number of unread notifications for displaying the badge in topbar navigation."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unread count retrieved successfully")
    })
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@CurrentUser UserPrincipal currentUser) {
        long count = notificationService.getUnreadCount(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("unreadCount", count)));
    }

    @Operation(
            summary = "Mark notification as read",
            description = "Marks a specific notification as read by notification ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification marked as read")
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAsRead(
            @Parameter(description = "ID of the notification to mark read", required = true, example = "1")
            @PathVariable("id") Long id,
            @CurrentUser UserPrincipal currentUser) {

        notificationService.markAsRead(id, currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", Collections.singletonMap("read", true)));
    }

    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications for the current authenticated user as read."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All notifications marked as read")
    })
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAllAsRead(@CurrentUser UserPrincipal currentUser) {
        notificationService.markAllAsRead(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", Collections.singletonMap("readAll", true)));
    }
}
