package com.maersk.referencedata.locationsconsumer.config;

import com.maersk.Geography.smds.operations.MSK.GeographyMessage;
import com.maersk.shared.kafka.configuration.KafkaReceiverBaseConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author Anders Clausen on 06/09/2021.
 */
@Configuration
@Getter
@Slf4j
public class KafkaConfig extends KafkaReceiverBaseConfiguration<String, GeographyMessage> {
}
