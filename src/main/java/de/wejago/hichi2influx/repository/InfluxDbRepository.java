package de.wejago.hichi2influx.repository;

<<<<<<< HEAD
import com.influxdb.client.WriteApi;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.config.InfluxDBConfig;
=======
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influx.config.InfluxDBConfig;
import de.wejago.hichi2influx.dto.BaseSensorEntry;
>>>>>>> 19665a3 ((wip) prepare working version)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class InfluxDbRepository {
    private static final int SCHEDULE_INTERVAL_PERSIST_TO_DB = 30000;
    private final InfluxDBConfig influxDBConfig;
    private final Queue<Point> measurementPoints = new ConcurrentLinkedQueue<>();

<<<<<<< HEAD
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
                if(numberOfPointsWritten > 1) {
                    log.info("writePoint() wrote : " + numberOfPointsWritten + " point(s).");
                }
            }
=======
    private InfluxDBClient influxDBClient;

    public Point writePoint(BaseSensorEntry sensorEntry) {
        updateConnectionIfNeeded();
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        Point point = sensorEntry.generateMeasurementPoint();
        writeApi.writePoint(point);

        return point;
    }

    private void updateConnectionIfNeeded() {
        //set the connection only if it is empty of not active
        if (influxDBClient == null || !influxDBClient.ping()) {
            influxDBClient = influxDBConfig.dbConnection();
>>>>>>> 19665a3 ((wip) prepare working version)
        }
    }
}
