package de.wejago.mqtt2influx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.repository.InfluxDbRepository;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
@RequiredArgsConstructor
public class JsonSubscriber implements IMqttMessageListener {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    private final ObjectMapper objectMapper;
    private final InfluxDbRepository influxDbRepository;
    private final Device device;

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        try {
            String receivedMessage = new String(mqttMessage.getPayload());

            if (StringUtils.isNotBlank(device.getOnlyMatch()) && receivedMessage.contains(device.getOnlyMatch())) {
                Map<String, Object> deviceToPointProperties = readDataFromMqttMessage(receivedMessage);
                Point point = generateMeasurementPoint(deviceToPointProperties, device);
                influxDbRepository.writePoint(point);
            }
        } catch (JsonProcessingException e) {
            log.error("objectMapper JsonProcessingException" + e);
        }
    }

    private Map<String, Object> readDataFromMqttMessage(String receivedMessage) throws JsonProcessingException {
        log.info("Received message to write: " + receivedMessage);
        JsonNode messageNode = objectMapper.readTree(receivedMessage);
        Map<String, String> deviceMappings = device.getMappings();
        return convertFromStringToMap(deviceMappings, messageNode);
    }

    private Map<String, Object> convertFromStringToMap(Map<String, String> deviceMappings, JsonNode messageNode) {
        Map<String, Object> deviceToPointProperties = new HashedMap();
        for (Iterator<Map.Entry<String, JsonNode>> it = messageNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getValue().isObject()) {
                addMapEntriesByExistingDeviceMappings(deviceMappings, deviceToPointProperties, entry);
            } else if (deviceMappings.containsKey(entry.getKey())) {
                deviceToPointProperties.put(entry.getKey(), entry.getValue().toString());
            }
        }

        return deviceToPointProperties;
    }

    private void addMapEntriesByExistingDeviceMappings(Map<String, String> deviceMappings, Map<String, Object> deviceToPointProperties,
                                                       Map.Entry<String, JsonNode> entry) {
        Map<String, String> messageValueMap = objectMapper.convertValue(entry.getValue(), Map.class);
        for (Map.Entry<String, String> messageValueItem : messageValueMap.entrySet()) {
            if (deviceMappings.containsKey(messageValueItem.getKey())) {
                String value = String.valueOf(messageValueItem.getValue());
                //we store all numbers as floats in influx
                if(NUMBER_PATTERN.matcher(value).matches()) {
                    try{
                        deviceToPointProperties.put(deviceMappings.get(messageValueItem.getKey()), Double.parseDouble(value));
                    } catch(NumberFormatException | ClassCastException e){
                        log.warn("NumberFormatException could not convert to double the value: " + messageValueItem.getValue());
                    }
                } else {
                    //strings should be stored as strings
                    deviceToPointProperties.put(deviceMappings.get(messageValueItem.getKey()), messageValueItem.getValue());
                }
            }
        }
    }

    private Point generateMeasurementPoint(Map<String, Object> deviceToPointProperties, Device device) {
        String sensorId = deviceToPointProperties.get(device.getMappings().get(device.getSensorId())).toString();
        //remove the sensor_id because it is String and cannot be added to the point as a field
        deviceToPointProperties.remove(device.getMappings().get(device.getSensorId()));
        return Point
            .measurement("sensor")
            .addTag("device_name", device.getName())
            .addTag("sensor_id", sensorId)
            .addFields(deviceToPointProperties)
            .time(Instant.now(), WritePrecision.MS);
    }
}
