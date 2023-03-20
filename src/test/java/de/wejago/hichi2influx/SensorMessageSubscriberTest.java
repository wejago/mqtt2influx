package de.wejago.hichi2influx;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wejago.hichi2influx.dto.SensorEntry;
import de.wejago.hichi2influx.dto.SensorMeasurement;
import de.wejago.hichi2influx.repository.InfluxDbRepository;
import de.wejago.hichi2influx.service.SensorMessageSubscriber;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SensorMessageSubscriberTest {
    private final String TIME_AS_STRING = "2022-02-16 02:25:32";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InfluxDbRepository influxDbRepository;

    @InjectMocks
    private SensorMessageSubscriber subscriber;

    @Test
    void messageArrived_withValidMessage_writesPointToInfluxDbRepository() throws Exception {
        // GIVEN
        String receivedMessage = "{\"Time\":\"2023-02-12T21:29:36\",\"SML\":{\"1_8_0\":22417.98}}".trim();
        SensorEntry expectedSensorEntry = generateTestSensorEntry();
        when(objectMapper.readValue(receivedMessage, SensorEntry.class)).thenReturn(expectedSensorEntry);

        // WHEN
        subscriber.messageArrived("test", new MqttMessage(receivedMessage.getBytes()));

        // THEN
        verify(influxDbRepository, times(1)).writePoint(any());
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

    private SensorEntry generateTestSensorEntry() {
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
}
