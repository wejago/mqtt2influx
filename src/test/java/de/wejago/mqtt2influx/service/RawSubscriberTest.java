package de.wejago.mqtt2influx.service;

import de.wejago.mqtt2influx.config.Device;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class RawSubscriberTest {
    private Device device;
    @Mock
    private RawSubscriberService rawSubscriberService;
    private RawSubscriber rawSubscriber;

    @BeforeEach
    public void setUp() {
        buildTestDevice();
        rawSubscriber = new RawSubscriber(rawSubscriberService, device);
    }

    @Test
    void messageArrived_WithValidMapping() {
        // GIVEN
        String receivedMessage = "208.20";
        String topic = "test/topic/yieldtotal";
        MqttMessage mqttMessage = new MqttMessage(receivedMessage.getBytes());

        // WHEN
        rawSubscriber.messageArrived(topic, mqttMessage);

        // THEN
        verify(rawSubscriberService, times(1)).updatePoint(device, "yieldtotal", 208.20);
    }

    @Test
    void messageArrived_WithInvalidMapping() {
        // GIVEN
        String receivedMessage = "208.20";
        String topic = "test/topic/invalid_mapping";
        MqttMessage mqttMessage = new MqttMessage(receivedMessage.getBytes());

        // WHEN
        rawSubscriber.messageArrived(topic, mqttMessage);

        // THEN
        verify(rawSubscriberService, never()).updatePoint(any(Device.class), anyString(), anyDouble());
    }

    private void buildTestDevice() {
        Map<String, String> deviceMappings = new HashMap<>();
        deviceMappings.put("yieldtotal", "Total Production");
        deviceMappings.put("yieldday", "Produced today");
        deviceMappings.put("power", "Current power");
        deviceMappings.put("powerdc", "Direct Current Power");
        deviceMappings.put("voltage", "Voltage");
        deviceMappings.put("current", "Current");
        deviceMappings.put("frequency", "Frequency");
        deviceMappings.put("powerfactor", "Power Factor");
        deviceMappings.put("efficiency", "Efficiency");
        deviceMappings.put("reactivepower", "Reactive Power");
        deviceMappings.put("temperature", "Temperature");

        device = new Device();
        device.setName("testName");
        device.setSensorId("device_id");
        device.setType("raw");
        device.setMappings(deviceMappings);
    }
}
