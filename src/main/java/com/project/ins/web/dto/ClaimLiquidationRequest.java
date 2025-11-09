package com.project.ins.web.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLiquidationRequest {

    private BigDecimal amount;

    private String note;

    private String declineReason;

}
