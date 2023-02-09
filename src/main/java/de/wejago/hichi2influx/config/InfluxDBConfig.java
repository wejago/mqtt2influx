package de.wejago.hichi2influx.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InfluxDBConfig {
    private final InfluxDBProperties influxDBProperties;

    public InfluxDBClient dbConnection() {
        return InfluxDBClientFactory.create(influxDBProperties.getUrl(), influxDBProperties.getToken().toCharArray(), influxDBProperties.getOrg(), influxDBProperties.getBucket());
    }
}
