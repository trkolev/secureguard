package com.project.ins.web.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
