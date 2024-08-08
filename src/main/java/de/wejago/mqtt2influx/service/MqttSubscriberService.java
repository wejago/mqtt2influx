package de.wejago.mqtt2influx.service;

import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.config.DevicesConfig;
import de.wejago.mqtt2influx.factory.SubscriberFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MqttSubscriberService {
    private final IMqttClient mqttClient;
    private final MqttConnectOptions mqttConnectOptions;
    private final DevicesConfig devicesConfig;
    private final SubscriberFactory subscriberFactory;

    @PostConstruct
    public void postConstruct() {
        try {
            mqttClient.connect(mqttConnectOptions);
            if (devicesConfig.getDevices() == null) {
                log.error("No devices in mqtt2influx-configuration.yaml");
                return;
            }
            for (final Device device : devicesConfig.getDevices()) {
                if (isDeviceValid(device)) {
                    final IMqttMessageListener subscriber = subscriberFactory.create(device);
                    mqttClient.subscribe(device.getTopic(), subscriber);
                    log.info("subscribed to topic: {}", device.getTopic());
                }
            }
        } catch (MqttException e) {
            log.error("Error connecting to MQTT client!", e);
        }
    }

    private static boolean isDeviceValid(Device device) {
        boolean isValid = true;
        if (device == null) {
            log.warn("Could NOT subscribe to device! The device is null.");
            isValid = false;
        } else if (StringUtils.isBlank(device.getTopic())) {
            log.warn("Could NOT subscribe to device! The device topic is blank for device: {}} Topic: {}", device,
                    device.getTopic());
            isValid = false;
        } else if (device.getMappings().isEmpty()) {
            log.warn("Could NOT subscribe to device! The deviceMapping is empty for device: {}", device);
            isValid = false;
        } else if (StringUtils.isBlank(device.getSensorId())) {
            log.warn("Could NOT subscribe to device! The sensorId is blank for device: {} SensorId: {}", device,
                    device.getSensorId());
            isValid = false;
        } else {
            if (device.getTopic().contains("ThisShouldBeChanged")) {
                log.warn("Service is currently using the packaged default configuration, " +
                                "you should adapt it to your needs!");
            }
        }
        return isValid;
    }
}
