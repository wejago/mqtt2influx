package de.wejago.hichi2influx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.dto.SensorEntry;
import de.wejago.hichi2influx.repository.InfluxDbRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorMessageSubscriber implements IMqttMessageListener {
    private final ObjectMapper objectMapper;
    private final InfluxDbRepository influxDbRepository;

    @Override public void messageArrived(String s, MqttMessage mqttMessage) {
        try {
            String receivedMessage = new String(mqttMessage.getPayload());
            if (receivedMessage.contains("1_8_0")) {
                log.info("Received message to write: " + receivedMessage);
                SensorEntry sensorEntry = objectMapper.readValue(receivedMessage, SensorEntry.class);
                Point point = generateMeasurementPoint(sensorEntry);
                influxDbRepository.writePoint(point);
            }
        } catch (JsonProcessingException e) {
            log.error("objectMapper JsonProcessingException" + e);
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
