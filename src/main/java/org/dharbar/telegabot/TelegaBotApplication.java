package org.dharbar.telegabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class TelegaBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegaBotApplication.class, args);
    }

}
