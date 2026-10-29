package tz.co.halotel.halokazi.alert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.halotel.halokazi.alert.dto.NotificationDto;
import tz.co.halotel.halokazi.alert.entity.Notification;
import tz.co.halotel.halokazi.alert.repository.NotificationRepository;
import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationDto> getNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50));
        Page<Notification> nPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<NotificationDto> dtos = nPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                dtos,
                nPage.getNumber(),
                nPage.getSize(),
                nPage.getTotalElements(),
                nPage.getTotalPages(),
                nPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, 0);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByNotificationIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notification.setReadAt(OffsetDateTime.now());
            notificationRepository.save(notification);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId, OffsetDateTime.now());
    }

    private NotificationDto mapToDto(Notification n) {
        return new NotificationDto(
                n.getNotificationId(),
                n.getTitle(),
                n.getMessage(),
                n.getRelatedEntityType(),
                n.getRelatedEntityId(),
                n.getIsRead() != null && n.getIsRead() == 1,
                n.getReadAt(),
                n.getCreatedAt()
        );
    }
}
