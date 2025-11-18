package com.project.ins.notification.client;

import com.project.ins.notification.client.dto.Notification;
import com.project.ins.notification.client.dto.SmsSendRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "notification-client", url = "http://localhost:8082/api/v1/")
public interface NotificationClient {

    @PostMapping("sms")
    String sendSms(SmsSendRequest smsSendRequest);

    @GetMapping("sms")
    ResponseEntity<List<Notification>> getNotifications(@RequestParam("userId")UUID ussrId);

    @DeleteMapping("sms")
    String deleteSms(@RequestParam("userId")UUID ussrId);

}
