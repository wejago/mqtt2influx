package de.wejago.hichi2influx.config;

import java.net.InetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttClientConfig {
    private static final String PROTOCOL_TCP = "tcp";

    private static final String CLIENT_ID= "mqtt-2-influx-db-application-" + InetAddress.getLoopbackAddress().getHostName();

    private final MqttProperties mqttProperties;

    @Bean
    public IMqttClient getMqttClient() {
        try (MemoryPersistence persistence = new MemoryPersistence()) {
            log.info("mqtt IP: " + mqttProperties.getBrokerIp() + " user: " + mqttProperties.getUsername() + "client ID: " + CLIENT_ID);
            return new MqttClient(buildBrokerUrl(), CLIENT_ID, persistence);
        } catch (MqttException e) {
            log.error("Error connecting to MQTT client!", e);
            return null;
        }
    }

    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(mqttProperties.getUsername());
        connectOptions.setPassword(mqttProperties.getPassword().toCharArray());
        connectOptions.setCleanSession(true);
        return connectOptions;
    }

    private String buildBrokerUrl() {
        UriComponents uriComponents = UriComponentsBuilder
            .newInstance()
            .scheme(PROTOCOL_TCP)
            .host(mqttProperties.getBrokerIp())
            .port(mqttProperties.getBrokerPort())
            .build();

        return uriComponents.toUriString();
    }
}
