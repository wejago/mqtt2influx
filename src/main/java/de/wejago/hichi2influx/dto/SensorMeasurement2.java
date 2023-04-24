package de.wejago.hichi2influx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorMeasurement2 {
    @JsonProperty("Total_in")
    private Double totalIn;

    @JsonProperty("Total_out")
    private Double totalOut;

    @JsonProperty("Power_curr")
    private Integer currentPower;

    @JsonProperty("device_id")
    private String deviceId;
}
