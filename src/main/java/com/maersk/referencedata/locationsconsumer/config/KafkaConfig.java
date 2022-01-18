package com.maersk.referencedata.locationsconsumer.config;

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
public class KafkaConfig extends KafkaReceiverBaseConfiguration<String, com.maersk.geography.smds.operations.msk.geographyMessage> {

    @Override
    protected ReceiverOptions<String, com.maersk.geography.smds.operations.msk.geographyMessage> kafkaReceiverOptions() {
        ReceiverOptions<String, com.maersk.geography.smds.operations.msk.geographyMessage> options = super.kafkaReceiverOptions();
        return options.addAssignListener(receiverPartitions -> receiverPartitions.forEach(ReceiverPartition::seekToBeginning));
    }
}
