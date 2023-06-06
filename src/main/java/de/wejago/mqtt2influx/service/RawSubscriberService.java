package de.wejago.mqtt2influx.service;

import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.config.MqttDataPoint;
import de.wejago.mqtt2influx.repository.KafkaRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Getter
public class RawSubscriberService {
    private final KafkaRepository kafkaRepository;

    // This map stores the points for each device to be written to the database
    // Key: Sensor ID of the device
    private final Map<String, MqttDataPoint> points = new ConcurrentHashMap<>();
    private static final int SCHEDULE_INTERVAL_PERSIST_TO_QUEUE = 60000;

    public void updatePoint(Device device, String key, Double measurement) {
        if (!points.containsKey(device.getSensorId())) {
            MqttDataPoint mqttDataPoint = createMqttDataPoint(device);
            points.put(device.getSensorId(), mqttDataPoint);
        }
        this.updateMqttDataFields(device, key, measurement);
    }

    @Scheduled(fixedRate = SCHEDULE_INTERVAL_PERSIST_TO_QUEUE)
    public void scheduleAddPointToQueue() {
        Iterator<Map.Entry<String, MqttDataPoint>> iterator = points.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, MqttDataPoint> entry = iterator.next();
            MqttDataPoint point = entry.getValue();

            if (point.getFields() != null
                && point.getFields().size() > 0) {
                point.setTime(Instant.now());
                kafkaRepository.writePoint(point);
                System.out.println("raw subscriber: " + point.getDevice_name());
                iterator.remove();
            }
        }
    }

    private void updateMqttDataFields(Device device, String key, Double measurement) {
        String pointKey = key;
        if(device.getMappings().containsKey(key)){
            pointKey = device.getMappings().get(key);
        }

        if (points.get(device.getSensorId()).getFields() != null
            && points.get(device.getSensorId()).getFields().size() > 0
            && points.get(device.getSensorId()).getFields().containsKey(key)) {
            // update existing measurement
            points.get(device.getSensorId()).getFields().replace(pointKey, measurement);
        } else {
            // add new measurement to the point
            points.get(device.getSensorId()).getFields().put(pointKey, measurement);
        }
    }

    private MqttDataPoint createMqttDataPoint(Device device) {
        MqttDataPoint mqttDataPoint = new MqttDataPoint();
        mqttDataPoint.setDevice_name(device.getName());
        mqttDataPoint.setSensor_id(device.getSensorId());
        mqttDataPoint.setFields(new HashMap<>());
        return mqttDataPoint;
    }
}
