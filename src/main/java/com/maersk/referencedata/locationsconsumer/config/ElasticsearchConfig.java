package com.maersk.referencedata.locationsconsumer.config;

import com.maersk.referencedata.locationsconsumer.monitoring.CustomElasticsearchReactiveHealthIndicator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.cloud.sleuth.instrument.web.client.TraceExchangeFilterFunction;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.time.Duration;

/**
 * Elasticsearch configuration class that gives us the
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchConfig extends AbstractReactiveElasticsearchConfiguration {

    private static final String LOCAL = "local";

    @Value("${spring.profiles.active}")
    private String activeProfile;
    @Value("${spring.elasticsearch.host}")
    private String elasticSearchHostname;
    @Value("${spring.elasticsearch.api-key}")
    private String apiKey;

    private final ConfigurableApplicationContext springContext;

    @Bean
    public ClientConfiguration clientConfiguration() {

        if (LOCAL.equals(activeProfile)) {
            return ClientConfiguration.builder()
                    .connectedTo(elasticSearchHostname)
                    .withWebClientConfigurer(webClient -> {
                        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                                .codecs(configurer -> configurer.defaultCodecs()
                                        .maxInMemorySize(-1))
                                .build();
                        return webClient.mutate().filter(TraceExchangeFilterFunction.create(springContext)).exchangeStrategies(exchangeStrategies).build();
                    })
                    .build();
        } else {

            return ClientConfiguration.builder() //
                    .connectedTo(elasticSearchHostname)
                    .usingSsl()
                    .withWebClientConfigurer(webClient -> {
                        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                                .codecs(configurer -> configurer.defaultCodecs()
                                        .maxInMemorySize(-1))
                                .build();
                        return webClient.mutate().filter(TraceExchangeFilterFunction.create(springContext)).exchangeStrategies(exchangeStrategies).build();
                    })
                    .withHeaders(() -> {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", "ApiKey " + apiKey);
                        return headers;
                    })
                    .build();
        }
    }

    @SneakyThrows
    @Bean
    @Override
    public ReactiveElasticsearchClient reactiveElasticsearchClient() {

        final ClientConfiguration clientConfiguration = clientConfiguration();

        log.info("Configuring Elastic search in profile {}", activeProfile);

        return ReactiveRestClients.create(clientConfiguration);
    }

    @Bean
    public ReactiveHealthContributor elasticsearchHealthContributor(ReactiveElasticsearchClient reactiveElasticsearchClient, ClientConfiguration clientConfiguration) {
        return new CustomElasticsearchReactiveHealthIndicator(reactiveElasticsearchClient, clientConfiguration);
    }
}