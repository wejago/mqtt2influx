package de.wejago.mqtt2influx;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class Mqtt2influxDbApplication {
    public static void main(String[] args) {
        SpringApplication.run(Mqtt2influxDbApplication.class, args);
    }
}
