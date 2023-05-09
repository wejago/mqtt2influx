package de.wejago.hichi2influx.config;

import java.util.Map;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Device {
    private String name;
    private String type;
    private String topic;
    private String only_match;

    private String sensor_id;
    private Map<String, String> mappings;
}
