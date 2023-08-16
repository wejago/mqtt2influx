package de.wejago.mqtt2influx.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.exceptions.InfluxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Log4j2
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
        } catch (Exception e) {
            log.warn("Failed to create writeApi: {}", e.getMessage(), e);
        }
    }

    InfluxDBClient getInfluxDbClient() throws InfluxException {
        return InfluxDBClientFactory.create(influxDBProperties.getUrl(),
                influxDBProperties.getToken().toCharArray(), influxDBProperties.getOrg(),
                influxDBProperties.getBucket());
    }
}
