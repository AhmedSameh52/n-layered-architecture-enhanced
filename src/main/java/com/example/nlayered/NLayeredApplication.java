package com.example.nlayered;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.nlayered.common.clients")
@EnableKafka
public class NLayeredApplication {

    public static void main(String[] args) {
        SpringApplication.run(NLayeredApplication.class, args);
    }
}