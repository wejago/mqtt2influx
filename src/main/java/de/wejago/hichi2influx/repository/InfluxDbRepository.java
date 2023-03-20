package de.wejago.hichi2influx.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.config.InfluxDBConfig;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
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
    private InfluxDBClient influxDBClient;
    private Queue<Point> measurementPoints = new LinkedBlockingQueue<>();
    private WriteApi writeApi;

    public void writePoint(Point point) {
        measurementPoints.add(point);
    }

    @Scheduled(fixedRate = SCHEDULE_INTERVAL_PERSIST_TO_DB)
    public void persistPointsToInflux() {
        if(!measurementPoints.isEmpty()){
            influxDBClient = influxDBConfig.createInfluxClient();
            int numberOfPointsWritten = 0;

            writeApi = influxDBConfig.getWriteApi();
            if(influxDBClient.ping() && writeApi != null) {
                while (!measurementPoints.isEmpty()) {
                    Point point = measurementPoints.poll();
                    writeApi.writePoint(point);
                    numberOfPointsWritten++;
                }
            }
            log.info("writePoint() wrote : " + numberOfPointsWritten + " point(s).");
        }
    }
}
