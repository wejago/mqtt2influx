package de.wejago.hichi2influx.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.config.InfluxDBConfig;
import de.wejago.hichi2influx.dto.SensorEntry;
import de.wejago.hichi2influx.dto.SensorMeasurement;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InfluxDbRepositoryTest {
    private static final String TIME_AS_STRING = "2022-02-16 02:25:32";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mock
    private WriteApi writeApi;
    @Mock
    private InfluxDBConfig influxDBConfig;
    @InjectMocks
    private InfluxDbRepository influxDbRepository;

    @BeforeEach
    public void setup() {
        influxDbRepository = new InfluxDbRepository(influxDBConfig);
    }

    @Test
    void shouldPersistPointsToInflux() {
        //GIVEN
        Point point = generateExpectedMeasurementPoint();
        influxDbRepository.writePoint(point);
        when(influxDBConfig.getWriteApi()).thenReturn(writeApi);

        //WHEN
        influxDbRepository.persistPointsToInflux();

        //THEN
        verify(writeApi, times(1)).writePoint(point);
    }

    @Test
    void shouldNotPersistPointsToInfluxWhenMeasurementPointsIsEmpty() {
        influxDbRepository.persistPointsToInflux();

        verify(writeApi, never()).writePoint(any());
    }

    @Test
    void shouldNotPersistPointsToInfluxWhenWriteApiIsNotAvailable() {
        //GIVEN
        Point point = generateExpectedMeasurementPoint();
        influxDbRepository.writePoint(point);
        when(influxDBConfig.getWriteApi()).thenReturn(null);

        //WHEN
        influxDbRepository.persistPointsToInflux();

        //THEN
        verify(writeApi, never()).writePoint(any());
    }

    public static SensorEntry generateTestSensorEntry() {
        SensorEntry testSensorEntry = new SensorEntry();
        testSensorEntry.setTime(LocalDateTime.parse(TIME_AS_STRING, formatter).atOffset(ZoneOffset.UTC).toString());
        SensorMeasurement sensorMeasurement = new SensorMeasurement();
        sensorMeasurement.setTotalConsumption(100.0);
        sensorMeasurement.setTariff1Consumption(50.0);
        sensorMeasurement.setTariff2Consumption(30.0);
        sensorMeasurement.setEnergyExport(20.0);
        sensorMeasurement.setCurrentConsumption(10.0);
        sensorMeasurement.setCurrentConsumptionPhase1(5.0);
        sensorMeasurement.setCurrentConsumptionPhase2(3.0);
        sensorMeasurement.setCurrentConsumptionPhase3(2.0);
        sensorMeasurement.setDeviceId("myDevice");
        testSensorEntry.setSml(sensorMeasurement);

        return testSensorEntry;
    }

    public static Point generateExpectedMeasurementPoint() {
        SensorEntry sensorEntry = generateTestSensorEntry();
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
                .time(LocalDateTime.parse(TIME_AS_STRING, formatter).toInstant(ZoneOffset.UTC), WritePrecision.MS);
    }
}
