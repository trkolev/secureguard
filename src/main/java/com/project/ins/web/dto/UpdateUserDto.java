package com.project.ins.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    @Size(min = 3, max = 12)
    @NotNull
    private String username;

    @Size(min = 3, max = 25)
    @NotNull
    private String firstName;

    @Size(min = 3, max = 25)
    @NotNull
    private String lastName;

    private String address;

    private String phoneNumber;

    @Email
    @NotNull
    private String email;

}
