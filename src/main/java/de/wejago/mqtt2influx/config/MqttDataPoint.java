package de.wejago.mqtt2influx.config;

import java.time.Instant;
import java.util.Map;
import lombok.Data;

@Data
public class MqttDataPoint {
    private String device_name;
    private String sensor_id;
    private Map<String, Object> fields;
    private Instant time;
}
