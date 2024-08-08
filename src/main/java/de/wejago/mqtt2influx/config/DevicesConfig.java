package de.wejago.mqtt2influx.config;

import de.wejago.mqtt2influx.factory.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties
@PropertySource(value = "classpath:mqtt2influx-configuration.yaml", factory = YamlPropertySourceFactory.class)
@PropertySource(value = "file:mqtt2influx-configuration.yaml", factory = YamlPropertySourceFactory.class,
        ignoreResourceNotFound=true)
public class DevicesConfig {
    private List<Device> devices;
}
