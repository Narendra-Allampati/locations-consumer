package com.maersk.referencedata.locationsconsumer.monitoring;
//
//import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.Status;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
//import org.springframework.http.HttpHeaders;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.Map;
//
//public class CustomElasticsearchReactiveHealthIndicator extends AbstractReactiveHealthIndicator {
//
//    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<>() {
//    };
//
//    private static final String RED_STATUS = "red";
//
//    private final ReactiveElasticsearchClient client;
//    private final ClientConfiguration clientConfiguration;
//
//    public CustomElasticsearchReactiveHealthIndicator(ReactiveElasticsearchClient reactiveElasticsearchClient, ClientConfiguration clientConfiguration) {
//        super("Elasticsearch health check failed");
//        this.client = reactiveElasticsearchClient;
//        this.clientConfiguration = clientConfiguration;
//    }
//
//    @Override
//    protected Mono<Health> doHealthCheck(Health.Builder builder) {
//        return this.client.execute((webClient) -> getHealth(builder, webClient));
//    }
//
//    private Mono<Health> getHealth(Health.Builder builder, WebClient webClient) {
//        return webClient.get() //
//                .uri("/_cluster/health/")//
//                .headers(headers -> {
//                    HttpHeaders suppliedHeaders = clientConfiguration.getHeadersSupplier().get();
//                    if (suppliedHeaders != null && suppliedHeaders != HttpHeaders.EMPTY) {
//                        headers.addAll(suppliedHeaders);
//                    }
//                })
//                .exchangeToMono((response) -> doHealthCheck(builder, response));
//    }
//
//    private Mono<Health> doHealthCheck(Health.Builder builder, ClientResponse response) {
//        if (response.statusCode().is2xxSuccessful()) {
//            return response.bodyToMono(STRING_OBJECT_MAP).map((body) -> getHealth(builder, body));
//        }
//        builder.down();
//        builder.withDetail("statusCode", response.rawStatusCode());
//        builder.withDetail("reasonPhrase", response.statusCode().getReasonPhrase());
//        return response.releaseBody().thenReturn(builder.build());
//    }
//
//    private Health getHealth(Health.Builder builder, Map<String, Object> body) {
//        String status = (String) body.get("status");
//        builder.status(RED_STATUS.equals(status) ? Status.OUT_OF_SERVICE : Status.UP);
//        builder.withDetails(body);
//        return builder.build();
//    }
//}