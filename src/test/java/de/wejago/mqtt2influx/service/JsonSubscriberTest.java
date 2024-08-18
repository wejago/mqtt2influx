package de.wejago.mqtt2influx.service;

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
    @Mock
    private InfluxDbRepository influxDbRepository;

    private Device device;
    private Device device2;
    private JsonSubscriber jsonSubscriber;
    private JsonSubscriber jsonSubscriber2;

    @BeforeEach
    public void setUp() {
        buildTestDevice1();
        buildTestDevice2();
        jsonSubscriber = new JsonSubscriber(influxDbRepository, device);
        jsonSubscriber2 = new JsonSubscriber(influxDbRepository, device2);
    }

    @Test
    void messageArrived_whenOnlyMatchFound() {
        // GIVEN
        String receivedMessage = "{\"Time\":\"2023-05-10T10:16:32\",\"\":{\"Total_in\":695.38,\"Total_out\":16.94,\"Power_curr\":1151," +
                                 "\"device_id\":\"012345affecaffee\"}}";
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
    void messageArrived_whenOnlyMatchFound2() {
        // GIVEN
        String receivedMessage = "{\"StatusSNS\":{\"Time\":\"2023-08-11T09:11:33\",\"SML\":{\"1_8_0\":25273.16,\"1_8_1\":25272.15,\"1_8_2\":1.02,\"2_8_0\":5.39,\"16_7_0\":-9.59,\"36_7_0\":81,\"56_7_0\":95,\"76_7_0\":-315,\"96_1_0\":\"012345affecaffee\"}}}";
        MqttMessage mqttMessage = new MqttMessage(receivedMessage.getBytes());
        Point generatedTestPoint = buildTestPoint2();

        // WHEN
        jsonSubscriber2.messageArrived("testTopic", mqttMessage);

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
    void messageArrived_whenOnlyMatchNotFound() {
        // GIVEN
        MqttMessage mqttMessage = new MqttMessage("testPayload".getBytes());

        // WHEN
        jsonSubscriber.messageArrived("testTopic", mqttMessage);

        // THEN
        verify(influxDbRepository, never()).writePoint(any());
    }

    @Test
    void messageArrived_whenJsonProcessingExceptionThrown() {
        // GIVEN
        MqttMessage mqttMessage = new MqttMessage("device_id".getBytes());

        // WHEN
        jsonSubscriber.messageArrived("testTopic", mqttMessage);

        // THEN
        verify(influxDbRepository, never()).writePoint(any());
    }

    @Test
    void messageArrived_noOnlyMatch() {

    }

    @Test
    void messageArrived_checkString() {
        // GIVEN
        Device deviceNext = buildTestDeviceNext();
        JsonSubscriber subscriber = new JsonSubscriber(influxDbRepository, deviceNext);
        String msg = "{\"device\": 121578518586686, \"queries\": 106900, \"blocked\": 36012, \"percent\": 33.7, \"safesearch\": 720, \"proctime\": 10.924999999999999}";
        MqttMessage mqttMessage = new MqttMessage(msg.getBytes());

        // WHEN
        subscriber.messageArrived("testTopic", mqttMessage);

        // THEN
        verify(influxDbRepository).writePoint(any());
    }

    private void buildTestDevice1() {
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

    private void buildTestDevice2() {
        Map<String, String> deviceMappings = new HashMap<>();
        deviceMappings.put("StatusSNS.SML.1_8_0", "Total Consumption");
        deviceMappings.put("StatusSNS.SML.2_8_0", "Total Production");
        deviceMappings.put("StatusSNS.SML.16_7_0", "Current Consumption");
        deviceMappings.put("StatusSNS.SML.96_1_0", "Device ID");
        device2 = new Device();
        device2.setName("testName");
        device2.setSensorId("StatusSNS.SML.96_1_0");
        device2.setOnlyMatch("1_8_0");
        device2.setMappings(deviceMappings);
    }

    private Device buildTestDeviceNext() {
        Map<String, String> deviceMappings = new HashMap<>();
        deviceMappings.put("queries", "Queries");
        deviceMappings.put("blocked", "Blocked");
        deviceMappings.put("percent", "Percent");
        deviceMappings.put("safesearch", "Safesearch");
        deviceMappings.put("device", "Device");
        deviceMappings.put("proctime", "Average processing time");
        Device device = new Device();
        device.setName("testName");
        device.setSensorId("device");
        device.setOnlyMatch("");
        device.setMappings(deviceMappings);
        return device;
    }

    private Point buildTestPoint() {
        return Point.measurement("sensor")
                .time(Instant.now(), WritePrecision.MS)
                .addTag("sensor_id", "012345affecaffee")
                .addTag("device_name", "testName")
                .addField("Total Consumption", 695.38)
                .addField("Total Production", 16.94)
                .addField("Current Consumption", 1151D);
    }

    private Point buildTestPoint2() {
        return Point.measurement("sensor")
                .time(Instant.now(), WritePrecision.MS)
                .addTag("sensor_id", "012345affecaffee")
                .addTag("device_name", "testName")
                .addField("Total Consumption", 25273.16)
                .addField("Total Production", 5.39)
                .addField("Current Consumption", -9.59d);
    }
}
