package de.wejago.hichi2influx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wejago.hichi2influx.dto.SensorEntry;
import de.wejago.hichi2influx.repository.InfluxDbRepository;
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
                log.info("Received message: " + receivedMessage);
                if(receivedMessage.contains("1_8_0")) {
                    SensorEntry sensorEntry = objectMapper.readValue(receivedMessage, SensorEntry.class);
                    influxDbRepository.writePoint(sensorEntry);
                }
            } catch (JsonProcessingException e) {
                log.error("objectMapper JsonProcessingException" + e);
            }
        }
    }
