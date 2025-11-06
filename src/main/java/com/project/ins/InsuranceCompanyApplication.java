package com.project.ins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class InsuranceCompanyApplication {

    public static final String LIGHT_GREEN = "\u001B[92m"; // Bright green
    public static final String RESET = "\u001B[0m"; // Reset color

    public static void main(String[] args) {

		SpringApplication.run(InsuranceCompanyApplication.class, args);
        System.out.println(LIGHT_GREEN + "Server Started" + RESET);
	}

}
