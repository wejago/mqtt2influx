package de.wejago.mqtt2influx.config;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
public class Device {
    private String name;
    private String type;
    private String topic;
    private String onlyMatch;

    private String sensorId;
    private Map<String, String> mappings;
}
