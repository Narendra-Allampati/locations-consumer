package com.maersk.referencedata.locationsconsumer.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.time.Duration;

/**
 * Elasticsearch configuration class that gives us the
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchConfig extends AbstractReactiveElasticsearchConfiguration {

    @Value("${spring.elasticsearch.host}")
    private String elasticSearchHostname;

    private final ConfigurableApplicationContext springContext;

    @Bean
    public ClientConfiguration clientConfiguration() {

            return ClientConfiguration.builder()
                    .connectedTo(elasticSearchHostname)
                    .withConnectTimeout(Duration.ofDays(3))
                    .withSocketTimeout(Duration.ofDays(3))
                    .withWebClientConfigurer(webClient -> {
                        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                                .codecs(configurer -> configurer.defaultCodecs()
                                        .maxInMemorySize(-1))
                                .build();
                        return webClient.mutate().exchangeStrategies(exchangeStrategies).build();
                    })
                    .build();
    }

    @SneakyThrows
    @Bean
    @Override
    public ReactiveElasticsearchClient reactiveElasticsearchClient() {

        final ClientConfiguration clientConfiguration = clientConfiguration();

        return ReactiveRestClients.create(clientConfiguration);
    }
}