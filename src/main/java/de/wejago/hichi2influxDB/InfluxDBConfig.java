package de.wejago.hichi2influxDB;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {
    @Value("${influxdb.url}")
    private String influxDBUrl;

    @Value("${influxdb.token}")
    private String influxDBToken;

    @Value("${influxdb.org}")
    private String influxDBOrganization;

    @Value("${influxdb.bucket}")
    private String influxDBBucket;
}
