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

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class JsonSubscriber implements IMqttMessageListener {
    private final InfluxDbRepository influxDbRepository;
    private final Device device;

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        final String receivedMessage = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);

        if (StringUtils.isBlank(device.getOnlyMatch()) || onlyMatches(receivedMessage)) {
            try {
                final Map<String, Object> flatJson = JsonFlattener.flattenAsMap(receivedMessage);
                final Map<String, Object> deviceToPointProperties = matchValues(device.getMappings(), flatJson);
                final Point point = generateMeasurementPoint(deviceToPointProperties, device);
                influxDbRepository.writePoint(point);
            } catch (RuntimeException e) {
                log.warn("Could not parse mqtt message: {} -> {}", e.getMessage(), receivedMessage, e);
            }
        } else {
            log.debug("Not matched message from device {} with mappings: {}", receivedMessage, device.getMappings());
        }
    }

    private boolean onlyMatches(final String receivedMessage) {
        return StringUtils.isNotBlank(device.getOnlyMatch()) && receivedMessage.contains(device.getOnlyMatch());
    }

    private static Map<String, Object> matchValues(Map<String, String> mappings, Map<String, Object> flatJson) {
        final Map<String, Object> result = new HashMap<>();
        mappings.forEach((source, dest) -> {
            final Object value = flatJson.get(source);
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

    private static Point generateMeasurementPoint(Map<String, Object> deviceToPointProperties, Device device) {
        final String sensorId = deviceToPointProperties.get(device.getMappings().get(device.getSensorId())).toString();
        // remove the sensor_id because it is String and cannot be added to the point as a field
        deviceToPointProperties.remove(device.getMappings().get(device.getSensorId()));
        log.debug("Create point for {} with mappings: {}", sensorId, deviceToPointProperties);
        return Point
                .measurement("sensor")
                .addTag("device_name", device.getName())
                .addTag("sensor_id", sensorId)
                .addFields(deviceToPointProperties)
                .time(Instant.now(), WritePrecision.MS);
    }
}
