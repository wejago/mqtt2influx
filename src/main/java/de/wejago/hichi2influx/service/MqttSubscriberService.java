package de.wejago.hichi2influx.service;

import de.wejago.hichi2influx.config.DevicesConfig;
import de.wejago.hichi2influx.config.MqttClientConfig;
import de.wejago.hichi2influx.config.MqttProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
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

    @PostConstruct
    public void postConstruct() {
        try {
            System.out.println(devicesConfig.getDevices().size());
            mqttClient.connect(mqttConnectOptions);
            log.info("topic: " + mqttProperties.getTopic() + " topic2: " + mqttProperties.getTopic2());
            mqttClient.subscribe(mqttProperties.getTopic(), sensorMessageSubscriber);
            mqttClient.subscribe(mqttProperties.getTopic2(), sensorMessageSubscriber);
            log.info("Successfully connected to MQTT client!");
        } catch (MqttException e) {
            log.error("Error connecting to MQTT client!", e);
        }
    }
}
