package de.wejago.hichi2influx.repository;

import com.influxdb.client.WriteApi;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.config.InfluxDBConfig;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
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
            WriteApi writeApi = influxDBConfig.getWriteApi();
            if (writeApi != null) {
                int numberOfPointsWritten = 0;
                while (!measurementPoints.isEmpty()) {
                    Point point = measurementPoints.poll();
                    writeApi.writePoint(point);
                    numberOfPointsWritten++;
                }
                //TODO - remove after 01.05.2023
                if (numberOfPointsWritten > 1) {
                    log.info("writePoint() wrote : " + numberOfPointsWritten + " point(s).");
                }
            }
        }
    }
}
