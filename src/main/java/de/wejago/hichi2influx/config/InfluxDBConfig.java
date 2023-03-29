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
    private WriteApi writeApi;

    public WriteApi getWriteApi() {
        if (writeApi == null || !influxDBClient.ping()) {
            createWriteApi();
        }
        return writeApi;
    }

    private void createWriteApi() {
        if (writeApi != null) {
            writeApi.close();
        }
        try {
            if (influxDBClient == null || !influxDBClient.ping()) {
                influxDBClient = getInfluxDbClient();
            }
            writeApi = influxDBClient.makeWriteApi();
        } catch (Exception e) { // <-- @Emo: try to find the specific exception here for disconnects
            log.warn("Failed to create writeApi: {}", e.getMessage(), e);
        }
    }

    InfluxDBClient getInfluxDbClient() throws InfluxException {
        log.info("url: " + influxDBProperties.getUrl() + " token: " + influxDBProperties.getToken() + " org: " +
                influxDBProperties.getOrg() + " bucket: " + influxDBProperties.getBucket());
        return InfluxDBClientFactory.create(influxDBProperties.getUrl(),
                influxDBProperties.getToken().toCharArray(), influxDBProperties.getOrg(),
                influxDBProperties.getBucket());

    }
}
