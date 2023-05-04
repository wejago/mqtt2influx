package de.wejago.hichi2influx.service;

import de.wejago.hichi2influx.config.Device;
import de.wejago.hichi2influx.config.DevicesConfig;
import de.wejago.hichi2influx.config.MqttProperties;
import de.wejago.hichi2influx.factory.JsonSubscriberFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttSubscriberService {
    private final SensorMessageSubscriber sensorMessageSubscriber;
    private final IMqttClient mqttClient;
    private final MqttConnectOptions mqttConnectOptions;

    private final MqttProperties mqttProperties;

    private final DevicesConfig devicesConfig;

    private final JsonSubscriberFactory jsonSubscriberFactory;

    @PostConstruct
    public void postConstruct() {
        try {
            mqttClient.connect(mqttConnectOptions);
            for (Device device : devicesConfig.getDevices()) {
                Subscriber subscriber = jsonSubscriberFactory.create(device);
                mqttClient.subscribe(device.getTopic(), subscriber);
                log.info("subscribed to topic: " + device.getTopic());
            }
        } catch (MqttException e) {
            log.error("Error connecting to MQTT client!", e);
        }
    }
}
