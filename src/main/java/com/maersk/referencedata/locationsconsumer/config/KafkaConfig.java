package com.maersk.referencedata.locationsconsumer.config;

import com.maersk.Geography.smds.operations.MSK.geographyMessage;
import com.maersk.shared.kafka.configuration.KafkaReceiverBaseConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;

/**
 * @author Anders Clausen on 06/09/2021.
 */
@Configuration
@Getter
@Slf4j
public class KafkaConfig extends KafkaReceiverBaseConfiguration<String, geographyMessage> {

    @Override
    protected ReceiverOptions<String, geographyMessage> kafkaReceiverOptions() {
        ReceiverOptions<String, geographyMessage> options = super.kafkaReceiverOptions();
        return options.addAssignListener(receiverPartitions -> receiverPartitions.forEach(ReceiverPartition::seekToBeginning));
    }
}
