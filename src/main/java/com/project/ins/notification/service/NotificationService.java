package com.project.ins.notification.service;

import com.project.ins.notification.client.NotificationClient;
import com.project.ins.notification.client.dto.Notification;
import com.project.ins.notification.client.dto.SmsSendRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @CacheEvict(value = "notifications", key = "#senderId")
    public void sendNotification(String phoneNumber, String message, UUID senderId) {

        SmsSendRequest smsSendRequest = SmsSendRequest.builder()
                .phoneNumber(phoneNumber)
                .message(message)
                .senderId(senderId)
                .build();

        try {
            notificationClient.sendSms(smsSendRequest);
        }catch (FeignException e){
            log.error("SMS send failed due to {}", e.getMessage());
        }

    }

    @Cacheable(value = "notifications", key = "#userId")
    public List<Notification> getNotifications(UUID userId) {

        ResponseEntity<List<Notification>> notifications = notificationClient.getNotifications(userId);
        return notifications.getBody() != null ? notifications.getBody() : List.of();
    }

    @Cacheable(value = "notifications", key = "#userId")
    public List<Notification> getNotificationsLimit(UUID userId) {
        ResponseEntity<List<Notification>> notifications = notificationClient.getNotifications(userId);
        List<Notification> body = notifications.getBody();

        if (body == null || body.isEmpty()) {
            return body != null ? body : List.of();
        }
        return body.stream().limit(3).toList();
    }

    @CacheEvict(value = "notifications", key = "#userId")
    public void deleteNotifications(UUID userId) {
        notificationClient.deleteSms(userId);
    }
}
