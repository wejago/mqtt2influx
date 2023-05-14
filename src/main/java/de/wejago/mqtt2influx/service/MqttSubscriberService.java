package de.wejago.mqtt2influx.service;

import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.config.DevicesConfig;
import de.wejago.mqtt2influx.factory.JsonSubscriberFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttSubscriberService {
    private final IMqttClient mqttClient;
    private final MqttConnectOptions mqttConnectOptions;
    private final DevicesConfig devicesConfig;
    private final JsonSubscriberFactory jsonSubscriberFactory;

    @PostConstruct
    public void postConstruct() {
        try {
            mqttClient.connect(mqttConnectOptions);
            for (Device device : devicesConfig.getDevices()) {
                if (device != null && StringUtils.isNotBlank(device.getTopic())
                    && device.getMappings().size() > 0
                    && StringUtils.isNotBlank(device.getSensorId())) {
                    IMqttMessageListener subscriber = jsonSubscriberFactory.create(device);
                    mqttClient.subscribe(device.getTopic(), subscriber);
                    log.info("subscribed to topic: " + device.getTopic());
                } else {
                    log.warn("could NOT subscribe to device: " + device);
                }
            }
        } catch (MqttException e) {
            log.error("Error connecting to MQTT client!", e);
        }
    }
}
