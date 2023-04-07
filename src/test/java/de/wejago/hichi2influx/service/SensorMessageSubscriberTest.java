package de.wejago.hichi2influx.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.dto.SensorEntry;
import de.wejago.hichi2influx.repository.InfluxDbRepository;
import de.wejago.hichi2influx.repository.InfluxDbRepositoryTest;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorMessageSubscriberTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InfluxDbRepository influxDbRepository;

    @InjectMocks
    private SensorMessageSubscriber subscriber;

    @Test
    void messageArrived_withValidMessage_writesPointToInfluxDbRepository() throws Exception {
        // GIVEN
        String receivedMessage = "{\"Time\":\"2023-02-16T02:25:32\",\"SML\":{\"1_8_0\":22417.98}}".trim();
        SensorEntry expectedSensorEntry = InfluxDbRepositoryTest.generateTestSensorEntry();
        when(objectMapper.readValue(receivedMessage, SensorEntry.class)).thenReturn(expectedSensorEntry);

        // WHEN
        subscriber.messageArrived("test", new MqttMessage(receivedMessage.getBytes()));

        // THEN
        ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
        verify(influxDbRepository, times(1))
                .writePoint(captor.capture());
        assertThat(captor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("time")
                .isEqualTo(InfluxDbRepositoryTest.generateExpectedMeasurementPoint());
    }

    @Test
    void messageArrived_withJsonMissingSensorField_ignoresMessage() throws Exception {
        // GIVEN
        String receivedMessage = "{\"value\":10}";

        // WHEN
        subscriber.messageArrived("test", new MqttMessage(receivedMessage.getBytes()));

        // THEN
        verifyNoInteractions(influxDbRepository);
    }
}
