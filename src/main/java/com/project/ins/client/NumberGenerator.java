package com.project.ins.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;



    @FeignClient(name = "number-generator", url = "http://localhost:8081/api/v1/")
    public interface NumberGenerator {

        @GetMapping("/policy-numbers")
        String getResponse();

        @GetMapping("/claim-numbers")
        String getClaimNumbers();

    }

