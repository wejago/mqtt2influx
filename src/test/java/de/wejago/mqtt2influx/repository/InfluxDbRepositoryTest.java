package de.wejago.mqtt2influx.repository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.influxdb.client.WriteApi;
import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.InfluxDBConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InfluxDbRepositoryTest {

    @Mock
    private InfluxDBConfig influxDBConfig;

    @Mock
    private WriteApi writeApi;

    @InjectMocks
    private InfluxDbRepository influxDbRepository;


    @Test
    void persistPointsToInflux_whenQueueNotEmpty() {
        //GIVEN
        Point point1 = Point.measurement("sensor").addTag("sensor_id", "test1");
        Point point2 = Point.measurement("test2").addTag("sensor_id", "test2");
        influxDbRepository.writePoint(point1);
        influxDbRepository.writePoint(point2);
        when(influxDBConfig.getWriteApi()).thenReturn(writeApi);

        //WHEN
        influxDbRepository.persistPointsToInflux();

        //THEN
        verify(writeApi, times(1)).writePoint(point1);
        verify(writeApi, times(1)).writePoint(point2);
        verifyNoMoreInteractions(writeApi);
    }

    @Test
    void persistPointsToInflux_whenQueueEmpty() {
        //WHEN
        influxDbRepository.persistPointsToInflux();

        //THEN
        verifyNoInteractions(writeApi);
    }
}
