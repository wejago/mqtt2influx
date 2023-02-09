package de.wejago.hichi2influx.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.wejago.hichi2influx.config.MqttClientConfig;
import de.wejago.hichi2influx.config.MqttProperties;
import de.wejago.hichi2influx.service.MqttSubscriberService;
import de.wejago.hichi2influx.service.SensorMessageSubscriber;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith({OutputCaptureExtension.class, MockitoExtension.class})
public class MqttSubscriberServiceTest {
    private final String TOPIC_TEST = "topic/test";
    @Mock
    private IMqttClient mqttClient;

    @Mock
    private MqttClientConfig mqttClientConfig;

    @Mock
    private MqttConnectOptions mqttConnectOptions;

    @Mock
    private SensorMessageSubscriber sensorMessageSubscriber;

    @Mock
    private MqttProperties mqttProperties;

    @InjectMocks
    private MqttSubscriberService mqttSubscriberService;

    @BeforeEach
    public void setup() {
        when(mqttClientConfig.getMqttClient()).thenReturn(mqttClient);
    }


    @Test
    void testConnectSuccess(CapturedOutput output) throws MqttException {
        // GIVEN
        when(mqttClientConfig.getMqttClient()).thenReturn(mqttClient);
        when(mqttClientConfig.getMqttConnectOptions()).thenReturn(mqttConnectOptions);
        when(mqttProperties.getTopic()).thenReturn(TOPIC_TEST);

        // WHEN
        mqttSubscriberService.postConstruct();

        // THEN
        verify(mqttClientConfig, times(1)).getMqttClient();
        verify(mqttClientConfig, times(1)).getMqttConnectOptions();
        verify(mqttClient, times(1)).connect(mqttConnectOptions);
        verify(mqttClient, times(1)).subscribe(TOPIC_TEST, sensorMessageSubscriber);
        assertThat(output.getOut()).contains("Successfully connected to MQTT client!");
    }

    @Test
    void testConnectThrowsException(CapturedOutput output) throws MqttException {
        // GIVEN
        when(mqttClientConfig.getMqttConnectOptions()).thenReturn(mqttConnectOptions);
        doThrow(new MqttException(1)).when(mqttClient).connect(mqttConnectOptions);

        // WHEN
        mqttSubscriberService.postConstruct();

        // THEN
        verify(mqttClient, times(1)).connect(mqttConnectOptions);
        verify(mqttClientConfig, times(1)).getMqttClient();
        verify(mqttClientConfig, times(1)).getMqttConnectOptions();
        assertThatThrownBy(() -> { throw new MqttException(1); })
            .isInstanceOf(MqttException.class)
            .hasMessage("Invalid protocol version");
        assertThat(output.getOut()).contains("Error connecting to MQTT client!");
    }
}
