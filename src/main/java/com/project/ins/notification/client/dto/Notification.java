package com.project.ins.notification.client.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Notification {

    private String message;

    private LocalDateTime createdAt;

    private String status;

    private UUID userId;

}
