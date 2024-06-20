package org.dharbar.telegabot;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.dharbar.telegabot.client.mono.MonoClient;
import org.dharbar.telegabot.service.statement.NotionStatementService;
import org.dharbar.telegabot.service.statement.notion.NotionService;
import org.dharbar.telegabot.trademanager.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class TelegaBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegaBotApplication.class, args);
    }

}
