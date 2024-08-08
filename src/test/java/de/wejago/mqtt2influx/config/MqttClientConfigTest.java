package de.wejago.mqtt2influx.config;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MqttClientConfigTest {
    private final String BROKER_IP = "localhost";
    private final String BROKER_PORT = "1883";
    private final String BROKER = "tcp://" + BROKER_IP + ":" + BROKER_PORT;

    private final String TEST_PASSWORD = "test-pass";

    @Mock
    private MqttProperties mqttProperties;

    @InjectMocks
    private MqttClientConfig mqttClientConfig;

    @Test
    void testSetMqttClientSuccess() {
        // GIVEN
        when(mqttProperties.getBrokerPort()).thenReturn(BROKER_PORT);
        when(mqttProperties.getBrokerIp()).thenReturn(BROKER_IP);

        // WHEN
        IMqttClient mqttClient = mqttClientConfig.getMqttClient();

        // THEN
        assertThat(mqttClient).isNotNull();
        assertThat(mqttClient.getServerURI()).isEqualTo(BROKER);
        assertThat(mqttClient.getClientId()).startsWith("mqtt-2-influx-db-application");
    }

    @Test
    void testSetMqttConnectOptionsReturnsNonNull() {
        // GIVEN
        MqttClientConfig anotherClientconfig = new MqttClientConfig(mqttProperties);
        when(mqttProperties.getPassword()).thenReturn(TEST_PASSWORD);

        // WHEN
        MqttConnectOptions connectOptions = anotherClientconfig.getMqttConnectOptions();

        // THEN
        assertThat(connectOptions).isNotNull();
        assertThat(connectOptions.getPassword()).isEqualTo(TEST_PASSWORD.toCharArray());
        assertThat(connectOptions.getUserName()).isNull();
    }
}
