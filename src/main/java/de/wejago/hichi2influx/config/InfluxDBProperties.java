package de.wejago.hichi2influx.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "influxdb")
@Component
@Getter
@Setter
public class InfluxDBProperties {
    private String url;

    private String token;

    private String org;

    private String bucket;
}
