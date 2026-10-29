package tz.co.halotel.halokazi.alert.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import tz.co.halotel.halokazi.alert.dto.NotificationDto;
import tz.co.halotel.halokazi.alert.entity.Notification;
import tz.co.halotel.halokazi.alert.repository.NotificationRepository;
import tz.co.halotel.halokazi.common.api.PageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        sampleNotification = new Notification();
        sampleNotification.setNotificationId(1L);
        sampleNotification.setUserId(10L);
        sampleNotification.setTitle("New Job Match");
        sampleNotification.setMessage("A job matching your alert has been posted");
        sampleNotification.setIsRead(0);
        sampleNotification.setCreatedAt(OffsetDateTime.now());
    }

    @Test
    void getNotifications_ShouldReturnList() {
        Page<Notification> page = new PageImpl<>(List.of(sampleNotification));
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(eq(10L), any(Pageable.class))).thenReturn(page);

        PageResponse<NotificationDto> result = notificationService.getNotifications(10L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("New Job Match", result.getContent().get(0).getTitle());
        assertFalse(result.getContent().get(0).isRead());
    }

    @Test
    void markAsRead_ShouldUpdateReadStatus() {
        when(notificationRepository.findByNotificationIdAndUserId(1L, 10L)).thenReturn(Optional.of(sampleNotification));

        notificationService.markAsRead(1L, 10L);

        assertEquals(1, sampleNotification.getIsRead());
        assertNotNull(sampleNotification.getReadAt());
        verify(notificationRepository, times(1)).save(sampleNotification);
    }

    @Test
    void markAllAsRead_ShouldInvokeRepository() {
        notificationService.markAllAsRead(10L);
        verify(notificationRepository, times(1)).markAllAsRead(eq(10L), any(OffsetDateTime.class));
    }
}
