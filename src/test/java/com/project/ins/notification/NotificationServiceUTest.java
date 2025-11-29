package com.project.ins.notification;

import com.project.ins.notification.client.NotificationClient;
import com.project.ins.notification.client.dto.Notification;
import com.project.ins.notification.client.dto.SmsSendRequest;
import com.project.ins.notification.service.NotificationService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceUTest {

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private NotificationService notificationService;

    private UUID testUserId;
    private String testPhoneNumber;
    private String testMessage;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testPhoneNumber = "+359888888888";
        testMessage = "Test message";
    }

    @Test
    void sendNotification_shouldCallNotificationClient() {
        when(notificationClient.sendSms(any(SmsSendRequest.class))).thenReturn("OK");

        notificationService.sendNotification(testPhoneNumber, testMessage, testUserId);

        verify(notificationClient).sendSms(any(SmsSendRequest.class));
    }

    @Test
    void sendNotification_shouldHandleFeignException() {
        when(notificationClient.sendSms(any(SmsSendRequest.class)))
                .thenThrow(mock(FeignException.class));

        assertDoesNotThrow(() -> notificationService.sendNotification(testPhoneNumber, testMessage, testUserId));

        verify(notificationClient).sendSms(any(SmsSendRequest.class));
    }

    @Test
    void getNotifications_shouldReturnNotificationsFromClient() {
        Notification notification1 = Notification.builder()
                .message("Notification 1")
                .createdAt(LocalDateTime.now())
                .build();
        Notification notification2 = Notification.builder()
                .message("Notification 2")
                .createdAt(LocalDateTime.now())
                .build();
        List<Notification> expectedNotifications = Arrays.asList(notification1, notification2);

        when(notificationClient.getNotifications(testUserId))
                .thenReturn(ResponseEntity.ok(expectedNotifications));

        List<Notification> result = notificationService.getNotifications(testUserId);

        verify(notificationClient).getNotifications(testUserId);
        assertEquals(expectedNotifications, result);
    }

    @Test
    void getNotifications_shouldReturnEmptyListWhenBodyIsNull() {
        when(notificationClient.getNotifications(testUserId))
                .thenReturn(ResponseEntity.ok(null));

        List<Notification> result = notificationService.getNotifications(testUserId);

        verify(notificationClient).getNotifications(testUserId);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNotificationsLimit_shouldReturnLimitedNotifications() {
        Notification notification1 = Notification.builder()
                .message("Notification 1")
                .createdAt(LocalDateTime.now())
                .build();
        Notification notification2 = Notification.builder()
                .message("Notification 2")
                .createdAt(LocalDateTime.now())
                .build();
        Notification notification3 = Notification.builder()
                .message("Notification 3")
                .createdAt(LocalDateTime.now())
                .build();
        Notification notification4 = Notification.builder()
                .message("Notification 4")
                .createdAt(LocalDateTime.now())
                .build();
        List<Notification> allNotifications = Arrays.asList(notification1, notification2, notification3, notification4);

        when(notificationClient.getNotifications(testUserId))
                .thenReturn(ResponseEntity.ok(allNotifications));

        List<Notification> result = notificationService.getNotificationsLimit(testUserId);

        verify(notificationClient).getNotifications(testUserId);
        assertEquals(3, result.size());
        assertEquals(notification1, result.get(0));
        assertEquals(notification2, result.get(1));
        assertEquals(notification3, result.get(2));
    }

    @Test
    void getNotificationsLimit_shouldReturnEmptyListWhenBodyIsNull() {
        when(notificationClient.getNotifications(testUserId))
                .thenReturn(ResponseEntity.ok(null));

        List<Notification> result = notificationService.getNotificationsLimit(testUserId);

        verify(notificationClient).getNotifications(testUserId);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNotificationsLimit_shouldReturnEmptyListWhenBodyIsEmpty() {
        when(notificationClient.getNotifications(testUserId))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        List<Notification> result = notificationService.getNotificationsLimit(testUserId);

        verify(notificationClient).getNotifications(testUserId);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteNotifications_shouldCallDeleteSms() {
        when(notificationClient.deleteSms(testUserId)).thenReturn("OK");

        notificationService.deleteNotifications(testUserId);

        verify(notificationClient).deleteSms(testUserId);
    }
}
