package de.wejago.mqtt2influx;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
@RequiredArgsConstructor
public class Mqtt2influxDbApplication {
	public static void main(String[] args) {
		SpringApplication.run(Mqtt2influxDbApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate) {
		return args -> {
			kafkaTemplate.send("mqtt-topic1", "hello kafka!");
		};
	}
}
