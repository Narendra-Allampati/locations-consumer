package com.maersk.referencedata.locationsconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class LocationsConsumerApplication {

	public static void main(String[] args) {
		Schedulers.enableMetrics();
		SpringApplication.run(LocationsConsumerApplication.class, args);
	}
}
