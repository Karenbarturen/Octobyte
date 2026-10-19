package com.octobyte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OctobyteApplication {

    public static void main(String[] args) {
        SpringApplication.run(OctobyteApplication.class, args);
    }
}
