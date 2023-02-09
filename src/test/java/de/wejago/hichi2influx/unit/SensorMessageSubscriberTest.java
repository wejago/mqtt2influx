package de.wejago.hichi2influx.unit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wejago.hichi2influx.dto.SensorEntry;
import de.wejago.hichi2influx.repository.InfluxDbRepository;
import de.wejago.hichi2influx.service.SensorMessageSubscriber;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SensorMessageSubscriberTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InfluxDbRepository influxDbRepository;

    @InjectMocks
    private SensorMessageSubscriber subscriber;

    @Test
    void messageArrived_withValidMessage_writesPointToInfluxDbRepository() throws Exception {
        // GIVEN
        String receivedMessage = "{\"Time\":\"2023-02-12T21:29:36\",\"SML\":{\"1_8_0\":22417.98}}";
        SensorEntry expectedSensorEntry = new SensorEntry();
        when(objectMapper.readValue(receivedMessage, SensorEntry.class)).thenReturn(expectedSensorEntry);

        // WHEN
        subscriber.messageArrived("test", new MqttMessage(receivedMessage.getBytes()));

        // THEN
        verify(influxDbRepository).writePoint(expectedSensorEntry);
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
