package de.wejago.hichi2influxDB;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InfluxDBConfig {
    @Value("${influxdb.url}")
    private String influxDBUrl;

    @Value("${influxdb.token}")
    private String influxDBToken;

    @Value("${influxdb.org}")
    private String influxDBOrganization;

    @Value("${influxdb.bucket}")
    private String influxDBBucket;


    public InfluxDBClient dbConnection() {
        return InfluxDBClientFactory.create(influxDBUrl, influxDBToken.toCharArray(), influxDBOrganization, influxDBBucket);
    }
}
