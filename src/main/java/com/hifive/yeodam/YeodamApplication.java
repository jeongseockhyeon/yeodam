package com.hifive.yeodam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YeodamApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeodamApplication.class, args);
    }

}
