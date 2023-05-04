package de.wejago.hichi2influx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.config.Device;
import de.wejago.hichi2influx.repository.InfluxDbRepository;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonSubscriber extends Subscriber{
    private final ObjectMapper objectMapper;
    private final InfluxDbRepository influxDbRepository;
    private final Device device;

    @Override public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        try {
            String receivedMessage = new String(mqttMessage.getPayload());

            if(device != null && device.getOnly_match() != null && receivedMessage.contains(device.getOnly_match())) {
                Map<String, Object> deviceToPointProperties = readDataFromMqttMessage(receivedMessage);
                Point point = generateMeasurementPoint(deviceToPointProperties);
                influxDbRepository.writePoint(point);
            }
        } catch (JsonProcessingException e) {
            log.error("objectMapper JsonProcessingException" + e);
        }
    }

    private Map readDataFromMqttMessage(String receivedMessage) throws JsonProcessingException{
        log.info("Received message to write: " + receivedMessage);
        Map<String, Object> messageMap
            = objectMapper.readValue(receivedMessage, new TypeReference<Map<String,Object>>(){});
        Map<String, String> deviceMappings = device.getMappings();
        Map<String, Object> deviceToPointProperties = new HashedMap();
        for (Map.Entry<String,Object> entry : messageMap.entrySet()) {
            if(entry.getValue() instanceof Map) {
                Map<String, String> messageValueMap = objectMapper.convertValue(entry.getValue(), Map.class);
                for(Map.Entry<String, String> messageValueItem : messageValueMap.entrySet()) {
                    if(deviceMappings.containsKey(messageValueItem.getKey())) {
                        deviceToPointProperties.put(messageValueItem.getKey(), messageValueItem.getValue());
                    }
                }
            } else if(deviceMappings.containsKey(entry.getValue())) {
                deviceToPointProperties.put(entry.getKey(), entry.getValue().toString());
            }
        }

        return deviceToPointProperties;
    }

    private Point generateMeasurementPoint(Map<String, Object> deviceToPointProperties) {
        String sensorId = deviceToPointProperties.get(device.getSensor_id()).toString();
        //remove the sensor_id because it is String and cannot be added to the point as a field
        deviceToPointProperties.remove(device.getSensor_id());
        return Point
            .measurement("sensor")
            .addTag("sensor_id", sensorId)
            .addFields(deviceToPointProperties)
            .time(Instant.now(), WritePrecision.MS);
    }
}
