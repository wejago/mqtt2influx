package de.wejago.hichi2influx.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.exceptions.InfluxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InfluxDBConfig {
    private final InfluxDBProperties influxDBProperties;
    private InfluxDBClient influxDBClient;

    public InfluxDBClient dbConnection() {
        log.info("url: " + influxDBProperties.getUrl() + " token: " + influxDBProperties.getToken() + " org: " + influxDBProperties.getOrg() + " bucket: " + influxDBProperties.getBucket());
        try{
            return InfluxDBClientFactory.create(influxDBProperties.getUrl(), influxDBProperties.getToken().toCharArray(), influxDBProperties.getOrg(), influxDBProperties.getBucket());
        } catch (InfluxException e) {
            log.error("Failed to connect to InfluxDB", e);
            return null;
        }
    }

    //set the connection only if it is empty or not active
    public InfluxDBClient createInfluxClient() {
        try {
            if (influxDBClient == null || !influxDBClient.ping()) {
                influxDBClient = dbConnection();
            }
            return influxDBClient;
        } catch (Exception e) {
            log.warn("Failed to connect to InfluxDB!");
            return null;
        }
    }

    public WriteApi getWriteApi() {
        try{
            return influxDBClient.makeWriteApi();
        } catch (Exception e) {
            log.warn("Failed to create writeApi from makeWriteApi()!");
        }
        return null;
    }
}
