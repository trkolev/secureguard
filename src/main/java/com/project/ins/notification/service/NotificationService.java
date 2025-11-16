package com.project.ins.notification.service;

import com.project.ins.notification.client.NotificationClient;
import com.project.ins.notification.client.dto.SmsSendRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
