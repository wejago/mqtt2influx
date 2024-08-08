package de.wejago.mqtt2influx.repository;

import com.influxdb.client.WriteApi;
import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.InfluxDBConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
@RequiredArgsConstructor
@Log4j2
@EnableScheduling
public class InfluxDbRepository {
    private static final int SCHEDULE_INTERVAL_PERSIST_TO_DB = 30000;
    private final InfluxDBConfig influxDBConfig;
    private final Queue<Point> measurementPoints = new ConcurrentLinkedQueue<>();

    public void writePoint(Point point) {
        measurementPoints.add(point);
    }

    @Scheduled(fixedRate = SCHEDULE_INTERVAL_PERSIST_TO_DB)
    public void persistPointsToInflux() {
        if (!measurementPoints.isEmpty()) {
            final WriteApi writeApi = influxDBConfig.getWriteApi();
            if (writeApi != null) {
                while (!measurementPoints.isEmpty()) {
                    final Point point = measurementPoints.poll();
                    writeApi.writePoint(point);
                }
            }
        }
    }
}
