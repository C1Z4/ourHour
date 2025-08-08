package com.ourhour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OurhourApplication {

    public static void main(String[] args) {
        SpringApplication.run(OurhourApplication.class, args);
    }

}
