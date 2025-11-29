package com.project.ins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
@EnableScheduling
public class InsuranceCompanyApplication {

    public static final String LIGHT_GREEN = "\u001B[92m";
    public static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        SpringApplication.run(InsuranceCompanyApplication.class, args);
        System.out.println(LIGHT_GREEN + "Server Started" + RESET);
    }
}
