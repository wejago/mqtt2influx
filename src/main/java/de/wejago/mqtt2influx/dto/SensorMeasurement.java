package de.wejago.mqtt2influx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorMeasurement {
    @JsonProperty("1_8_0")
    private Double totalConsumption;

    @JsonProperty("1_8_1")
    private Double tariff1Consumption;

    @JsonProperty("1_8_2")
    private Double tariff2Consumption;

    @JsonProperty("2_8_0")
    private Double energyExport;

    @JsonProperty("16_7_0")
    private Double currentConsumption;

    @JsonProperty("36_7_0")
    private Double currentConsumptionPhase1;

    @JsonProperty("56_7_0")
    private Double currentConsumptionPhase2;

    @JsonProperty("76_7_0")
    private Double currentConsumptionPhase3;

    @JsonProperty("96_1_0")
    private String deviceId;
}
