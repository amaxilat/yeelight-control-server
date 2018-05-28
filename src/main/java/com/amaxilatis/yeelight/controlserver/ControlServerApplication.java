package com.amaxilatis.yeelight.controlserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ControlServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ControlServerApplication.class, args);
    }
}
