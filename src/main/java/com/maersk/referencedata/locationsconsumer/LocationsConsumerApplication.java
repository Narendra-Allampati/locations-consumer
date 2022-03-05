package com.maersk.referencedata.locationsconsumer;

import org.neo4j.driver.Driver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;

@SpringBootApplication
public class LocationsConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocationsConsumerApplication.class, args);
	}

	@Bean
	ReactiveNeo4jTransactionManager reactiveTransactionManager(Driver driver, ReactiveDatabaseSelectionProvider databaseSelectionProvider) {
		return new ReactiveNeo4jTransactionManager(driver, databaseSelectionProvider);
	}

}
