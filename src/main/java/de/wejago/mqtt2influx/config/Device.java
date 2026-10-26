package de.wejago.mqtt2influx.config;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private List<ValueLimit> limits;

    /**
     * Checks a value against the configured limits of its (mapped) field name. Values of fields without
     * a limit, and non-numeric values, are always accepted.
     */
    public boolean isPlausible(final String field, final Object value) {
        if (limits == null || !(value instanceof Number number)) {
            return true;
        }
        return limits.stream()
                .filter(limit -> limit.getField().equals(field))
                .allMatch(limit -> limit.isWithin(number.doubleValue()));
    }
}
