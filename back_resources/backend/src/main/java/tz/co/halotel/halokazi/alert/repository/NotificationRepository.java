package tz.co.halotel.halokazi.alert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.alert.entity.Notification;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Notification> findByNotificationIdAndUserId(Long notificationId, Long userId);

    long countByUserIdAndIsRead(Long userId, Integer isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = 1, n.readAt = :readAt WHERE n.userId = :userId AND n.isRead = 0")
    void markAllAsRead(@Param("userId") Long userId, @Param("readAt") OffsetDateTime readAt);
}
