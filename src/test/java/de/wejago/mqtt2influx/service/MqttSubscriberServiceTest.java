package de.wejago.mqtt2influx.service;

import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.config.DevicesConfig;
import de.wejago.mqtt2influx.factory.SubscriberFactory;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ OutputCaptureExtension.class, MockitoExtension.class })
class MqttSubscriberServiceTest {
    @Mock
    private IMqttClient mqttClient;
    @Mock
    private MqttConnectOptions mqttConnectOptions;
    @Mock
    private DevicesConfig devicesConfig;
    @Mock
    private SubscriberFactory subscriberFactory;
    @InjectMocks
    private MqttSubscriberService mqttSubscriberService;

    @Test
    void postConstruct_success() throws MqttException {
        //GIVEN
        Device device1 = initializeTestDevice(1);
        Device device2 = initializeTestDevice(2);
        when(devicesConfig.getDevices()).thenReturn(Arrays.asList(device1, device2));

        //WHEN
        mqttSubscriberService.postConstruct();

        //THEN
        verify(mqttClient, times(1)).connect(mqttConnectOptions);
        verify(mqttClient, times(1)).subscribe(device1.getTopic(), subscriberFactory.create(device1));
        verify(mqttClient, times(1)).subscribe(device2.getTopic(), subscriberFactory.create(device2));
    }

    @Test
    void postConstruct_noDevices() throws MqttException {
        when(devicesConfig.getDevices()).thenReturn(null);

        mqttSubscriberService.postConstruct();

        verify(mqttClient, times(0)).subscribe(any(), any(IMqttMessageListener.class));
    }

    @Test
    void testConnectThrowsException(CapturedOutput output) throws MqttException {
        // GIVEN
        doThrow(new MqttException(1)).when(mqttClient).connect(mqttConnectOptions);

        // WHEN
        mqttSubscriberService.postConstruct();

        // THEN
        verify(mqttClient, times(1)).connect(mqttConnectOptions);
        assertThatThrownBy(() -> {throw new MqttException(1);})
            .isInstanceOf(MqttException.class);
        assertThat(output.getOut()).contains("Error connecting to MQTT client!");
    }

    @Test
    void postConstruct_deviceNull() throws MqttException {
        //GIVEN
        when(devicesConfig.getDevices()).thenReturn(Collections.singletonList(null));

        //WHEN
        mqttSubscriberService.postConstruct();

        //THEN
        verify(mqttClient, never()).subscribe(anyString(), any());
    }

    @Test
    void postConstruct_topicBlank() throws MqttException {
        //GIVEN
        Device device = initializeTestDevice(1);
        device.setTopic("");

        when(devicesConfig.getDevices()).thenReturn(Collections.singletonList(device));

        //WHEN
        mqttSubscriberService.postConstruct();

        //THEN
        verify(mqttClient, never()).subscribe(anyString(), any());
    }

    @Test
    void postConstruct_mappingEmpty() throws MqttException {
        //GIVEN
        Device device = initializeTestDevice(1);
        device.setMappings(new HashMap<>());
        when(devicesConfig.getDevices()).thenReturn(Collections.singletonList(device));

        //WHEN
        mqttSubscriberService.postConstruct();

        //THEN
        verify(mqttClient, never()).subscribe(anyString(), any());
    }

    @Test
    void postConstruct_sensorIdBlank() throws MqttException {
        //GIVEN
        Device device = initializeTestDevice(1);
        device.setSensorId("");
        when(devicesConfig.getDevices()).thenReturn(Collections.singletonList(device));

        //WHEN
        mqttSubscriberService.postConstruct();

        //THEN
        verify(mqttClient, never()).subscribe(anyString(), any());
    }

    private Device initializeTestDevice(int deviceNum) {
        Map<String, String> deviceMapping = new HashMap<>();
        deviceMapping.put("key" + deviceNum, "value" + deviceNum);
        Device device = new Device();
        device.setTopic("testTopic" + deviceNum);
        device.setSensorId("testSensorId" + deviceNum);
        device.setMappings(deviceMapping);

        return device;
    }
}
