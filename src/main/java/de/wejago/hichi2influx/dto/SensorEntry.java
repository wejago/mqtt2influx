package de.wejago.hichi2influx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorEntry extends BaseSensorEntry{
    @JsonProperty("SML")
    private SensorMeasurement sml;

    @Override public Point generateMeasurementPoint() {
        return Point.measurement("sensor")
                    .addTag("sensor_id", sml.getDeviceId())
                    .addField("totalConsumption(1_8_0)", sml.getTotalConsumption())
                    .addField("tariff1Consumption(1_8_1)", sml.getTariff1Consumption())
                    .addField("tariff2Consumption(1_8_2)", sml.getTariff2Consumption())
                    .addField("energyExport(2_8_0)", sml.getEnergyExport())
                    .addField("currentConsumption(16_7_0)", sml.getCurrentConsumption())
                    .addField("currentConsumptionPhase1(36_7_0)", sml.getCurrentConsumptionPhase1())
                    .addField("currentConsumptionPhase2(56_7_0)", sml.getCurrentConsumptionPhase2())
                    .addField("currentConsumptionPhase3(76_7_0)", sml.getCurrentConsumptionPhase3())
                    .time(Instant.now(), WritePrecision.MS);
    }
}
