package de.wejago.mqtt2influx.service;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.repository.InfluxDbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Log4j2
@RequiredArgsConstructor
public class JsonSubscriber implements IMqttMessageListener {
    private final InfluxDbRepository influxDbRepository;
    private final Device device;

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        String receivedMessage = new String(mqttMessage.getPayload());

        if (StringUtils.isNotBlank(device.getOnlyMatch()) && receivedMessage.contains(device.getOnlyMatch())) {
            try {
                Map<String, Object> flatJson = JsonFlattener.flattenAsMap(receivedMessage);
                Map<String, Object> deviceToPointProperties = matchValues(device.getMappings(), flatJson);
                String deviceKeyValue = device.getMappings().get(device.getSensorId());
                if(deviceKeyValue == null || !deviceToPointProperties.containsKey(deviceKeyValue) ||
                        deviceToPointProperties.size() < 2) {
                    logConfigurationProblems(deviceKeyValue, deviceToPointProperties, flatJson);
                } else {
                    Point point = generateMeasurementPoint(deviceToPointProperties, device, deviceKeyValue);
                    influxDbRepository.writePoint(point);
                }
            } catch (RuntimeException e) {
                log.warn("Could not parse mqtt message: {} -> {}", e.getMessage(), receivedMessage);
            }
        }
    }

    private Map<String, Object> matchValues(Map<String, String> mappings, Map<String, Object> flatJson) {
        Map<String, Object> result = new HashMap<>();
        mappings.forEach((source, dest) -> {
            Object value = flatJson.get(source);
            if (value != null) {
                if (value instanceof Integer integer) {
                    result.put(dest, integer.doubleValue());
                } else {
                    result.put(dest, value);
                }
            }
        });
        return result;
    }

    private Point generateMeasurementPoint(Map<String, Object> deviceToPointProperties,
                                           Device device, String deviceKeyValue) {

        String sensorId = deviceToPointProperties.get(deviceKeyValue).toString();
        // remove the sensor_id because it is String and cannot be added to the point as a field
        deviceToPointProperties.remove(deviceKeyValue);
        return Point
                .measurement("sensor")
                .addTag("device_name", device.getName())
                .addTag("sensor_id", sensorId)
                .addFields(deviceToPointProperties)
                .time(Instant.now(), WritePrecision.MS);
    }

    private void logConfigurationProblems(String deviceKeyValue, Map<String, Object> deviceToPointProperties,
                                          Map<String, Object> flatJson) {
        log.warn("There is something wrong with the configuration for device {}!", device.getName());
        if(deviceKeyValue == null || !deviceToPointProperties.containsKey(deviceKeyValue)) {
            log.warn("Device ID not found! Specified: '{}' In Mesage: {}", deviceKeyValue,
                    deviceToPointProperties.containsKey(deviceKeyValue));
        }
        if(deviceToPointProperties.size() < 2) {
            log.warn("There are no entries for your mappings!");
            log.info("Mappings:");
            device.getMappings().forEach((source, dest) -> log.info(" - {}", source));
            log.info("JsonPaths in message:");
            flatJson.forEach((key, value) -> log.info(" - {}", key));
        }
    }
}
