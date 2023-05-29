package de.wejago.mqtt2influx.service;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.repository.InfluxDbRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Getter
public class RawSubscriberService {
    private final InfluxDbRepository influxDbRepository;

    // This map stores the points for each device to be written to the database
    // Key: Sensor ID of the device
    private final Map<String, Point> points = new ConcurrentHashMap<>();
    private static final int SCHEDULE_INTERVAL_PERSIST_TO_QUEUE = 60000;

    public void updatePoint(Device device, String key, Double measurement) {
        if (!points.containsKey(device.getSensorId())) {
            Point point = createPoint(device);
            points.put(device.getSensorId(), point);
        }

        points.get(device.getSensorId()).addField(device.getMappings().get(key), measurement);
    }

    @Scheduled(fixedRate = SCHEDULE_INTERVAL_PERSIST_TO_QUEUE)
    public void scheduleAddPointToQueue() {
        Iterator<Map.Entry<String, Point>> iterator = points.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Point> entry = iterator.next();
            Point point = entry.getValue();

            if (point.hasFields()) {
                point.time(Instant.now(), WritePrecision.MS);
                influxDbRepository.writePoint(point);
                iterator.remove();
            }
        }
    }

    private Point createPoint(Device device) {
        return Point.measurement("sensor")
                    .addTag("device_name", device.getName())
                    .addTag("sensor_id", device.getSensorId());
    }
}
