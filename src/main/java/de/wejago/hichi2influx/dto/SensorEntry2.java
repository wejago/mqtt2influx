package de.wejago.hichi2influx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@JsonDeserialize(using = SensorEntryDeserializer.class)
@Getter
@Setter
public class SensorEntry2 extends BaseSensorEntry{
    @JsonProperty("")
    private SensorMeasurement2 sml;

    @Override public Point generateMeasurementPoint() {
        return Point.measurement("sensor")
                    .addTag("sensor_id", sml.getDeviceId())
                    .addField("Total_in", sml.getTotalIn())
                    .addField("Total_out", sml.getTotalOut())
                    .addField("CurrentPower(Power_curr)", sml.getCurrentPower())
                    .time(Instant.now(), WritePrecision.MS);
    }
}
