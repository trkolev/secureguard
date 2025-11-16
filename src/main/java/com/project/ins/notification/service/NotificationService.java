package com.project.ins.notification.service;

import com.project.ins.notification.client.NotificationClient;
import com.project.ins.notification.client.dto.Notification;
import com.project.ins.notification.client.dto.SmsSendRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Notification> getNotifications(UUID userId) {

        ResponseEntity<List<Notification>> notifications = notificationClient.getNotifications(userId);
        return notifications.getBody();
    }

    public List<Notification> getNotificationsLimit(UUID userId) {

        ResponseEntity<List<Notification>> notifications = notificationClient.getNotifications(userId);

        if(notifications.getBody().isEmpty()){
            return notifications.getBody();
        }
        return notifications.getBody().stream().limit(3).toList();
    }

}
