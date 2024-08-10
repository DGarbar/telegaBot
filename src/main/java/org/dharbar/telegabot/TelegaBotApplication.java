package org.dharbar.telegabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class TelegaBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegaBotApplication.class, args);
    }

}
