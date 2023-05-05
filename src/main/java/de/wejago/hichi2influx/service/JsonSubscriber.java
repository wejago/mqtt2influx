package de.wejago.hichi2influx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.config.Device;
import de.wejago.hichi2influx.repository.InfluxDbRepository;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
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
        JsonNode messageNode = objectMapper.readTree(receivedMessage);
        Map<String, String> deviceMappings = device.getMappings();
        Map<String, Object> deviceToPointProperties = convertFromStringToMap(deviceMappings, messageNode);

        return deviceToPointProperties;
    }

    private Map convertFromStringToMap(Map<String, String> deviceMappings, JsonNode messageNode) {
        Map<String, Object> deviceToPointProperties = new HashedMap();
        for (Iterator<Map.Entry<String, JsonNode>> it = messageNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getValue().isObject()) {
                addMapEntriesByExistingDeviceMappings(deviceMappings, deviceToPointProperties, entry);
            } else if(deviceMappings.containsKey(entry.getValue())) {
                deviceToPointProperties.put(entry.getKey(), entry.getValue().toString());
            }
        }

        return deviceToPointProperties;
    }

    private void addMapEntriesByExistingDeviceMappings(Map<String, String> deviceMappings, Map<String, Object> deviceToPointProperties, Map.Entry<String, JsonNode> entry) {
        Map<String, String> messageValueMap = objectMapper.convertValue(entry.getValue(), Map.class);
        for(Map.Entry<String, String> messageValueItem : messageValueMap.entrySet()) {
            if(deviceMappings.containsKey(messageValueItem.getKey())) {
                deviceToPointProperties.put(messageValueItem.getKey(), messageValueItem.getValue());
            }
        }
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
