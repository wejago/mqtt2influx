package de.wejago.hichi2influx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.influxdb.client.write.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseSensorEntry {
    @JsonProperty("Time")
    private String time;

    public abstract Point generateMeasurementPoint();
}
