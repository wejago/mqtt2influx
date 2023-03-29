package de.wejago.hichi2influx.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.influxdb.client.WriteApi;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class InfluxDbConfigWriteApiTest {
    private final String INFLUX_URL = "http://localhost";
    private final String INFLUX_TOKEN = "testToken";
    private final String INFLUX_ORG = "test-organization";
    private final String INFLUX_BUCKET = "test-bucket1";

    @Mock
    WriteApi writeApi;
    @InjectMocks
    InfluxDBConfig influxDBConfig;

    @Test
    void getWriteApi_returnsNull_whenInfluxDBClientIsNull() {
        //GIVEN
        influxDBConfig = new InfluxDBConfig(new InfluxDBProperties());

        //WHEN
        WriteApi actualWriteApi = influxDBConfig.getWriteApi();

        //THEN
        assertThat(actualWriteApi).isNull();
    }

    @Test
    void getWriteApi_returnsNotNull_whenInfluxDBClientIsNotNull() {
        //GIVEN
        InfluxDBProperties influxDBProperties = new InfluxDBProperties();
        influxDBProperties.setOrg(INFLUX_ORG);
        influxDBProperties.setUrl(INFLUX_URL);
        influxDBProperties.setBucket(INFLUX_BUCKET);
        influxDBProperties.setToken(INFLUX_TOKEN);
        influxDBConfig = new InfluxDBConfig(influxDBProperties);
        influxDBConfig.getInfluxDbClient();

        //WHEN
        WriteApi actualWriteApi = influxDBConfig.getWriteApi();

        //THEN
        assertThat(actualWriteApi).isNotNull();
    }
}
