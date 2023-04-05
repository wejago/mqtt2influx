package de.wejago.hichi2influx.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.exceptions.InfluxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith({ OutputCaptureExtension.class, MockitoExtension.class})
class InfluxDBConfigTest {
    private final String INFLUX_URL = "http://localhost";
    private final String INFLUX_TOKEN = "testToken";
    private final String INFLUX_ORG = "test-organization";
    private final String INFLUX_BUCKET = "test-bucket1";
    private final String INFLUX_EXCEPTION_MSG = "Unable to connect to InfluxDB";

    @Mock
    private InfluxDBProperties influxDBProperties;

    @InjectMocks
    InfluxDBConfig influxDBConfig;

    @BeforeEach
    public void setup() {
        when(influxDBProperties.getUrl()).thenReturn(INFLUX_URL);
        when(influxDBProperties.getOrg()).thenReturn(INFLUX_ORG);
        when(influxDBProperties.getToken()).thenReturn(INFLUX_TOKEN);
        when(influxDBProperties.getBucket()).thenReturn(INFLUX_BUCKET);
   }

    @Test
    void testDbConnectionSuccess() {
        //WHEN
        InfluxDBClient client = influxDBConfig.getInfluxDbClient();

        //THEN
        assertThat(client).isNotNull();
    }

    @Test
    void testDbConnectionFailure() {
        when(InfluxDBClientFactory.create(influxDBProperties.getUrl(),
                                          influxDBProperties.getToken().toCharArray(),
                                          influxDBProperties.getOrg(),
                                          influxDBProperties.getBucket()))
            .thenThrow(new InfluxException(INFLUX_EXCEPTION_MSG));

        assertThatThrownBy(() -> influxDBConfig.getInfluxDbClient())
            .isInstanceOf(InfluxException.class)
            .hasMessage(INFLUX_EXCEPTION_MSG);
    }

    @Test
    void shouldReturnNullWhenUnableToConnectToInfluxDB(CapturedOutput output) {
        String INVALID_INFLUX_URL = "invalid-url";
        when(influxDBProperties.getUrl()).thenReturn(INVALID_INFLUX_URL);

        assertThatThrownBy(() -> {
            influxDBConfig.getInfluxDbClient();
        }).isInstanceOf(InfluxException.class);
    }
}
