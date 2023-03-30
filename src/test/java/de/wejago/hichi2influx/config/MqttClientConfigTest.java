package de.wejago.hichi2influx.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.wejago.hichi2influx.config.MqttClientConfig;
import de.wejago.hichi2influx.config.MqttProperties;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        assertThat(mqttClient.getClientId()).isEqualTo("hichi-2-influx-db-application");
    }

    @Test
    void testSetMqttConnectOptionsReturnsNonNull() {
        // GIVEN
        MqttClientConfig mqttClientConfig = new MqttClientConfig(mqttProperties);
        when(mqttProperties.getPassword()).thenReturn(TEST_PASSWORD);

        // WHEN
        MqttConnectOptions connectOptions = mqttClientConfig.getMqttConnectOptions();

        // THEN
        assertThat(connectOptions).isNotNull();
        assertThat(connectOptions.getPassword()).isEqualTo(TEST_PASSWORD.toCharArray());
        assertThat(connectOptions.getUserName()).isNull();
    }
}
