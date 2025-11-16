package com.project.ins.notification.client.dto;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsSendRequest {

    private String phoneNumber;

    private String message;

    private UUID senderId;

}

