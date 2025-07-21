package ru.practicum.ewm.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.client.StatsServer;

@Configuration
public class StatClientConfig {

    @Bean
    public StatsServer statClient(DiscoveryClient discoveryClient,
                                  @Value("${discovery.services.stats-server-id}") String statsServerId) {
        return new StatsServer(discoveryClient, statsServerId);
    }
}