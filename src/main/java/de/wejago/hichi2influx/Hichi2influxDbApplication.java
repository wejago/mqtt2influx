package de.wejago.hichi2influx;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class Hichi2influxDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(Hichi2influxDbApplication.class, args);
	}
}
