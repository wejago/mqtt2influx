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
                if (isDeviceValid(device)) {
                    IMqttMessageListener subscriber = jsonSubscriberFactory.create(device);
                    mqttClient.subscribe(device.getTopic(), subscriber);
                    log.info("subscribed to topic: " + device.getTopic());
                }
            }
        } catch (MqttException e) {
            log.error("Error connecting to MQTT client!", e);
        }
    }

    private static boolean isDeviceValid(Device device) {
        if(device == null) {
            log.warn("Could NOT subscribe to device! The device is null.");
            return false;
        } else if(StringUtils.isBlank(device.getTopic())) {
            log.warn("Could NOT subscribe to device! The device topic is blank for device: " + device + " Topic: " + device.getTopic());
            return false;
        } else if(device.getMappings().size() == 0) {
            log.warn("Could NOT subscribe to device! The deviceMapping is empty for device: "+ device);
            return false;
        } else if(StringUtils.isBlank(device.getSensorId())) {
            log.warn("Could NOT subscribe to device! The sendorId is blank for device: " + device + " SensorId: " + device.getSensorId());
            return false;
        }

        return true;
    }
}
