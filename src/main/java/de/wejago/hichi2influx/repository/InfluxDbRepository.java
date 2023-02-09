package de.wejago.hichi2influx.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.config.InfluxDBConfig;
import de.wejago.hichi2influx.dto.SensorEntry;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InfluxDbRepository {
    private final InfluxDBConfig influxDBConfig;

    private InfluxDBClient influxDBClient;

    public Point writePoint(SensorEntry sensorEntry) {
        updateConnectionIfNeeded();
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        Point point = generateMeasurementPoint(sensorEntry);
        writeApi.writePoint(point);

        return point;
    }

    private void updateConnectionIfNeeded() {
        //set the connection only if it is empty of not active
        if(influxDBClient == null || !influxDBClient.ping()) {
            influxDBClient = influxDBConfig.dbConnection();
        }
    }

    private static Point generateMeasurementPoint(SensorEntry sensorEntry) {
        return Point.measurement("sensor")
                    .addTag("sensor_id", sensorEntry.getSml().getDeviceId())
                    .addField("totalConsumption(1_8_0)", sensorEntry.getSml().getTotalConsumption())
                    .addField("tariff1Consumption(1_8_1)", sensorEntry.getSml().getTariff1Consumption())
                    .addField("tariff2Consumption(1_8_2)", sensorEntry.getSml().getTariff2Consumption())
                    .addField("energyExport(2_8_0)", sensorEntry.getSml().getEnergyExport())
                    .addField("currentConsumption(16_7_0)", sensorEntry.getSml().getCurrentConsumption())
                    .addField("currentConsumptionPhase1(36_7_0)", sensorEntry.getSml().getCurrentConsumptionPhase1())
                    .addField("currentConsumptionPhase2(56_7_0)", sensorEntry.getSml().getCurrentConsumptionPhase2())
                    .addField("currentConsumptionPhase3(76_7_0)", sensorEntry.getSml().getCurrentConsumptionPhase3())
                    .time(Instant.now(), WritePrecision.MS);
    }
}
