package de.wejago.hichi2influx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorEntry {
    @JsonProperty("Time")
    private String time;

    @JsonProperty("SML")
    private SensorMeasurement sml;
}
