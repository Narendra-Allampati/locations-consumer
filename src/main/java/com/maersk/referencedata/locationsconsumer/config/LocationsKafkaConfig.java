package com.maersk.referencedata.locationsconsumer.config;

import com.maersk.facility.smds.operations.msk.facilityMessage;
import com.maersk.geography.smds.operations.msk.geographyMessage;
import com.maersk.shared.kafka.configuration.MetricAwareKafkaConsumerFactory;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.micrometer.core.instrument.Metrics;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.ObjectUtils;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Getter
@Slf4j
public class LocationsKafkaConfig {
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.facilities.client-id}")
    private String facilitiesClientId;
    @Value("${kafka.consumer.locations.client-id}")
    private String locationsClientId;
    @Value("${kafka.client-id}")
    private String clientId;
    @Value("${kafka.username:}")
    private String username;
    @Value("${kafka.password:}")
    private String password;
    @Value("${kafka.login-module:org.apache.kafka.common.security.plain.PlainLoginModule}")
    private String loginModule;
    @Value("${kafka.sasl-mechanism:PLAIN}")
    private String saslMechanism;
    @Value("${kafka.security-protocol:SASL_SSL}")
    private String securityProtocol;
    @Value("${kafka.consumer.consumer-group}")
    private String consumerGroup;
    @Value("${kafka.consumer.locations.topic}")
    private String consumerLocationsTopicName;
    @Value("${kafka.consumer.facilities.topic}")
    private String consumerFacilitiesTopicName;
    @Value("${kafka.consumer.offset-auto-reset:latest}")
    private String consumerOffsetAutoReset;
    @Value("${kafka.consumer.max-poll-records:100}")
    private String consumerMaxPollRecords;
    @Value("${kafka.consumer.max-poll-timeout:5000}")
    private long pollTimeout;
    @Value("${kafka.consumer.max-fetch-size-bytes}")
    private Integer maxRequestSizeBytes;
    @Value("${kafka.schema-registry.url}")
    private String schemaRegistryUrl;
    @Value("${kafka.schema-registry.username}")
    private String schemaRegistryUsername;
    @Value("${kafka.schema-registry.password}")
    private String schemaRegistryPassword;
    private final ConsumerFactory.Listener<String, geographyMessage> consumerFactoryListener;

    public LocationsKafkaConfig() {
        this.consumerFactoryListener = new MicrometerConsumerListener(Metrics.globalRegistry);
    }

    public LocationsKafkaConfig(ConsumerFactory.Listener<String, geographyMessage> consumerFactoryListener) {
        this.consumerFactoryListener = consumerFactoryListener;
    }

    @Bean
    KafkaReceiver<String, geographyMessage> locationsKafkaReceiver() {
        MetricAwareKafkaConsumerFactory<String, geographyMessage> metricAwareProducerFactory = new MetricAwareKafkaConsumerFactory<>(this.consumerFactoryListener);
        Map<String, Object> stringObjectMap = this.kafkaConsumerProperties();
        stringObjectMap.put("client.id", locationsClientId);
        ReceiverOptions<String, geographyMessage> options = ReceiverOptions.create(stringObjectMap);
        options = options.addAssignListener(receiverPartitions -> receiverPartitions.forEach(ReceiverPartition::seekToBeginning));
        options = options.pollTimeout(Duration.ofMillis(this.pollTimeout)).subscription(List.of(consumerLocationsTopicName));
        return KafkaReceiver.create(metricAwareProducerFactory, options);
    }

    @Bean
    KafkaReceiver<String, facilityMessage> facilityKafkaReceiver() {
        MetricAwareKafkaConsumerFactory<String, facilityMessage> metricAwareProducerFactory = new MetricAwareKafkaConsumerFactory(this.consumerFactoryListener);
        Map<String, Object> stringObjectMap = this.kafkaConsumerProperties();
        stringObjectMap.put("client.id", facilitiesClientId);
        ReceiverOptions<String, facilityMessage> options = ReceiverOptions.create(stringObjectMap);
        options = options.addAssignListener(receiverPartitions -> receiverPartitions.forEach(ReceiverPartition::seekToBeginning));
        options = options.pollTimeout(Duration.ofMillis(this.pollTimeout)).subscription(List.of(this.consumerFacilitiesTopicName));
        return KafkaReceiver.create(metricAwareProducerFactory, options);
    }

    protected Map<String, Object> kafkaConsumerProperties() {
        Map<String, Object> kafkaPropertiesMap = new HashMap();
        kafkaPropertiesMap.put("bootstrap.servers", this.bootstrapServers);
        kafkaPropertiesMap.put("key.deserializer", ErrorHandlingDeserializer.class);
        kafkaPropertiesMap.put("value.deserializer", ErrorHandlingDeserializer.class);
        kafkaPropertiesMap.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
        kafkaPropertiesMap.put("spring.deserializer.value.delegate.class", KafkaAvroDeserializer.class);
        kafkaPropertiesMap.put("auto.offset.reset", this.consumerOffsetAutoReset);
        kafkaPropertiesMap.put("enable.auto.commit", false);
        kafkaPropertiesMap.put("max.poll.records", this.consumerMaxPollRecords);
        kafkaPropertiesMap.put("max.partition.fetch.bytes", this.maxRequestSizeBytes);
        kafkaPropertiesMap.put("schema.registry.url", this.schemaRegistryUrl);
        kafkaPropertiesMap.put("basic.auth.credentials.source", "USER_INFO");
        kafkaPropertiesMap.put("basic.auth.user.info", this.schemaRegistryUsername + ":" + this.schemaRegistryPassword);
        kafkaPropertiesMap.put("specific.avro.reader", true);
        kafkaPropertiesMap.put("group.id", this.consumerGroup);
//        kafkaPropertiesMap.put("client.id", this.clientId);
        if (!ObjectUtils.isEmpty(this.username)) {
            kafkaPropertiesMap.put("security.protocol", this.securityProtocol);
            kafkaPropertiesMap.put("sasl.mechanism", this.saslMechanism);
            String saslJassConfig = String.format("%s required username=\"%s\" password=\"%s\" ;", this.loginModule, this.username, this.password);
            kafkaPropertiesMap.put("sasl.jaas.config", saslJassConfig);
        }

        return kafkaPropertiesMap;
    }

//    protected ReceiverOptions<String, geographyMessage> kafkaReceiverOptions() {
//        ReceiverOptions<String, geographyMessage> options = ReceiverOptions.create(this.kafkaConsumerProperties());
//        return options.pollTimeout(Duration.ofMillis(this.pollTimeout)).subscription(List.of(this.consumerTopicName));
//    }

    public String getBootstrapServers() {
        return this.bootstrapServers;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getLoginModule() {
        return this.loginModule;
    }

    public String getSaslMechanism() {
        return this.saslMechanism;
    }

    public String getSecurityProtocol() {
        return this.securityProtocol;
    }

    public String getConsumerGroup() {
        return this.consumerGroup;
    }

    public String getConsumerLocationsTopicName() {
        return this.consumerLocationsTopicName;
    }

    public String getConsumerFacilitiesTopicName() {
        return this.consumerFacilitiesTopicName;
    }

    public String getConsumerOffsetAutoReset() {
        return this.consumerOffsetAutoReset;
    }

    public String getConsumerMaxPollRecords() {
        return this.consumerMaxPollRecords;
    }

    public long getPollTimeout() {
        return this.pollTimeout;
    }

    public Integer getMaxRequestSizeBytes() {
        return this.maxRequestSizeBytes;
    }

    public String getSchemaRegistryUrl() {
        return this.schemaRegistryUrl;
    }

    public String getSchemaRegistryUsername() {
        return this.schemaRegistryUsername;
    }

    public String getSchemaRegistryPassword() {
        return this.schemaRegistryPassword;
    }

    public ConsumerFactory.Listener<String, geographyMessage> getConsumerFactoryListener() {
        return this.consumerFactoryListener;
    }

    public void setBootstrapServers(final String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setLoginModule(final String loginModule) {
        this.loginModule = loginModule;
    }

    public void setSaslMechanism(final String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    public void setSecurityProtocol(final String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public void setConsumerGroup(final String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setConsumerLocationsTopicName(final String consumerLocationsTopicName) {
        this.consumerLocationsTopicName = consumerLocationsTopicName;
    }

    public void setConsumerFacilitiesTopicName(final String consumerFacilitiesTopicName) {
        this.consumerFacilitiesTopicName = consumerFacilitiesTopicName;
    }

    public void setConsumerOffsetAutoReset(final String consumerOffsetAutoReset) {
        this.consumerOffsetAutoReset = consumerOffsetAutoReset;
    }

    public void setConsumerMaxPollRecords(final String consumerMaxPollRecords) {
        this.consumerMaxPollRecords = consumerMaxPollRecords;
    }

    public void setPollTimeout(final long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    public void setMaxRequestSizeBytes(final Integer maxRequestSizeBytes) {
        this.maxRequestSizeBytes = maxRequestSizeBytes;
    }

    public void setSchemaRegistryUrl(final String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    public void setSchemaRegistryUsername(final String schemaRegistryUsername) {
        this.schemaRegistryUsername = schemaRegistryUsername;
    }

    public void setSchemaRegistryPassword(final String schemaRegistryPassword) {
        this.schemaRegistryPassword = schemaRegistryPassword;
    }
}
