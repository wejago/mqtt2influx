package de.wejago.mqtt2influx.service;

import de.wejago.mqtt2influx.config.Device;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RequiredArgsConstructor
public class RawSubscriber implements IMqttMessageListener {
    private final RawSubscriberService rawSubscriberService;
    private final Device device;

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        final String receivedMessage = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);

        final Map<String, String> deviceMappings = device.getMappings();
        final String measurementKey = topic.substring(topic.lastIndexOf('/') + 1);

        if (deviceMappings.containsKey(measurementKey)) {
            final Double measurementValue = Double.valueOf(receivedMessage);
            rawSubscriberService.updatePoint(device, measurementKey, measurementValue);
        }
    }
}
