package de.wejago.mqtt2influx.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.repository.InfluxDbRepository;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JsonSubscriberTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private InfluxDbRepository influxDbRepository;

    private Device device;
    private JsonSubscriber jsonSubscriber;

    @BeforeEach
    public void setUp() {
        buildTestDevice();
        jsonSubscriber = new JsonSubscriber(objectMapper, influxDbRepository, device);
    }

    @Test
    void messageArrived_whenOnlyMatchFound() throws Exception {
        // GIVEN
        String receivedMessage = "{\"Time\":\"2023-05-10T10:16:32\",\"\":{\"Total_in\":695.38,\"Total_out\":16.94,\"Power_curr\":1151," +
                                 "\"device_id\":\"0a01454d480000b22b25\"}}";
        MqttMessage mqttMessage = new MqttMessage(receivedMessage.getBytes());
        Point generatedTestPoint = buildTestPoint();

        // WHEN
        jsonSubscriber.messageArrived("testTopic", mqttMessage);

        // THEN
        ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
        verify(influxDbRepository, times(1)).writePoint(captor.capture());
        assertThat(captor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("time")
                .ignoringFields("precision")
                .isEqualTo(generatedTestPoint);
    }

    @Test
    void messageArrived_whenOnlyMatchNotFound() throws Exception {
        // GIVEN
        MqttMessage mqttMessage = new MqttMessage("testPayload".getBytes());

        // WHEN
        jsonSubscriber.messageArrived("testTopic", mqttMessage);

        // THEN
        verify(influxDbRepository, never()).writePoint(any());
    }

    @Test
    void messageArrived_whenJsonProcessingExceptionThrown() throws Exception {
        // GIVEN
        MqttMessage mqttMessage = new MqttMessage("device_id".getBytes());

        // WHEN
        jsonSubscriber.messageArrived("testTopic", mqttMessage);

        // THEN
        verify(influxDbRepository, never()).writePoint(any());
    }

    private void buildTestDevice() {
        Map<String, String> deviceMappings = new HashMap<>();
        deviceMappings.put("Total_in", "Total Consumption");
        deviceMappings.put("Total_out", "Total Production");
        deviceMappings.put("Power_curr", "Current Consumption");
        deviceMappings.put("device_id", "Device ID");
        device = new Device();
        device.setName("testName");
        device.setSensorId("device_id");
        device.setOnlyMatch("device_id");
        device.setMappings(deviceMappings);
    }

    private Point buildTestPoint() {
        return Point.measurement("sensor")
                .time(Instant.now(), WritePrecision.MS)
                .addTag("sensor_id", "0a01454d480000b22b25")
                .addTag("device_name", "testName")
                .addField("Total Consumption", 695.38)
                .addField("Total Production", 16.94)
                .addField("Current Consumption", 1151D);
    }
}
