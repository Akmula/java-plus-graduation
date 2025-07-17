package ru.practicum.ewm.main.config;

import ru.practicum.ewm.client.StatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatClientConfig {

    @Bean
    public StatClient statClient(@Value("${stat-server-url}") String statServerUrl) {
        return new StatClient(statServerUrl);
    }
}