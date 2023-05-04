package de.wejago.hichi2influx.config;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class Device {
    private String name;
    private String type;
    private String topic;
    private String only_match;

    private String sensor_id;
    private Map<String, String> mappings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getOnly_match() {
        return only_match;
    }

    public void setOnly_match(String only_match) {
        this.only_match = only_match;
    }

    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public Map<String, String> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    @Override public String toString() {
        return "Device{" +
               "name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", topic='" + topic + '\'' +
               ", onlyMatch='" + only_match + '\'' +
               ", mappings=" + mappings +
               '}';
    }
}
