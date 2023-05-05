package de.wejago.hichi2influx.config;

import de.wejago.hichi2influx.factory.YamlPropertySourceFactory;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "")
@PropertySource(value = "classpath:mqtt2influx-configuration.yaml", factory = YamlPropertySourceFactory.class)
public class DevicesConfig {
    private List<Device> devices;
}
