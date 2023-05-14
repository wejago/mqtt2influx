package de.wejago.mqtt2influx.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "mqtt")
@Component
@Getter
@Setter
public class MqttProperties {
    private String brokerIp;

    private String brokerPort;

    private String username;

    private String password;
}
