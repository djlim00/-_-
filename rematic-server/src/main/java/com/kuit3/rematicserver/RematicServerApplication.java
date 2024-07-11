package com.kuit3.rematicserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RematicServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RematicServerApplication.class, args);
    }

}
