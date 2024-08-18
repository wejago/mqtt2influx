package de.wejago.mqtt2influx.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class MqttClientConfig {
    private static final String PROTOCOL_TCP = "tcp";
    private final MqttProperties mqttProperties;

    @Bean
    public IMqttClient getMqttClient() {
        try (MemoryPersistence persistence = new MemoryPersistence()) {
            final String clientId = "mqtt-2-influx-db-application-" + InetAddress.getLocalHost().getHostName();
            log.info("mqtt IP: {} user: {} client ID: {}", mqttProperties.getBrokerIp(),
                    mqttProperties.getUsername(), clientId);
            return new MqttClient(buildBrokerUrl(), clientId, persistence);
        } catch (MqttException e) {
            log.error("Error connecting to MQTT client!", e);
            return null;
        } catch (UnknownHostException e) {
            log.error("Error UnknownHostException!", e);
            return null;
        }
    }

    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        final MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(mqttProperties.getUsername());
        connectOptions.setPassword(mqttProperties.getPassword().toCharArray());
        connectOptions.setCleanSession(true);
        return connectOptions;
    }

    private String buildBrokerUrl() {
        final UriComponents uriComponents = UriComponentsBuilder
            .newInstance()
            .scheme(PROTOCOL_TCP)
            .host(mqttProperties.getBrokerIp())
            .port(mqttProperties.getBrokerPort())
            .build();

        return uriComponents.toUriString();
    }
}
