package de.wejago.mqtt2influx.config;

import lombok.Data;

@Data
public class ValueLimit {
    private String field;
    private Double min;
    private Double max;

    public boolean isWithin(final double value) {
        return (min == null || value >= min) && (max == null || value <= max);
    }
}
