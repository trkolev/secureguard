package com.project.ins.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    @NotNull
    @Size(min = 6, max = 25)
    private String currentPassword;

    @NotNull
    @Size(min = 6, max = 25)
    private String newPassword;

    @NotNull
    @Size(min = 6, max = 25)
    private String confirmPassword;
}
