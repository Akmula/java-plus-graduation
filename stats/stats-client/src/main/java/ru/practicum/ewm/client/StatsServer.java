package ru.practicum.ewm.client;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.net.URI;
import java.util.List;

public class StatsServer {
    private final DiscoveryClient discoveryClient;
    private final String statsServerId;

    public StatsServer(DiscoveryClient discoveryClient, String statsServerId) {
        this.discoveryClient = discoveryClient;
        this.statsServerId = statsServerId;
    }

    public void sendHit(EndpointHitDto hitDto) {
        restClient().post()
                .uri("/hit")
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("unique", unique);

        if (start != null && !start.isBlank()) {
            uriBuilder.queryParam("start", start);
        }
        if (end != null && !end.isBlank()) {
            uriBuilder.queryParam("end", end);
        }
        if (uris != null && !uris.isEmpty()) {
            uriBuilder.queryParam("uris", uris.toArray());
        }

        String uri = uriBuilder.build().toUriString();

        List<ViewStatsDto> response = restClient().get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return response != null ? response : List.of();
    }

    private RetryTemplate getRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServerId)
                    .getFirst();
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + statsServerId,
                    exception
            );
        }
    }

    private RestClient restClient() {

        URI baseUri = makeUri();

        return RestClient.builder().baseUrl(baseUri.toString()).build();
    }

    private URI makeUri() {
        ServiceInstance instance = getRetryTemplate().execute(cxt -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort());
    }
}