package com.dietapp.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dietapp"})
public class DietAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(DietAppApplication.class, args);
    }
}
