package com.project.ins.web.dto;

import com.project.ins.policy.model.Policy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {

    @NotNull
    private LocalDateTime incidentDate;

    @NotEmpty
    private String description;

    @NotNull
    private Policy clientPolicy;

}
