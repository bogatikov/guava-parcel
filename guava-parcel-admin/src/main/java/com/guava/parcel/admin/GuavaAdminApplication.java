package com.guava.parcel.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableReactiveFeignClients
@SpringBootApplication
public class GuavaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuavaAdminApplication.class, args);
    }
}
