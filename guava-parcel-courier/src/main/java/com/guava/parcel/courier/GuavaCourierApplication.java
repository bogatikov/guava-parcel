package com.guava.parcel.courier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableReactiveFeignClients
@SpringBootApplication
public class GuavaCourierApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuavaCourierApplication.class, args);
    }
}
