package de.wejago.hichi2influx.config;

import de.wejago.hichi2influx.factory.YamlPropertySourceFactory;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "")
@PropertySource(value = "classpath:mqtt2influx-configuration.yaml", factory = YamlPropertySourceFactory.class)
public class DevicesConfig {
    private List<Device> devices;

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
