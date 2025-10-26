package com.project.ins.web.dto;

import com.project.ins.policy.model.PolicyName;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequest {

    @NotNull
    private PolicyName policyName;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private String coverageDescription;

    @NotNull
    private BigDecimal coverageAmount;

    @NotNull
    private BigDecimal premiumAmount;

    @NotNull
    private LocalDate endDate;

}
