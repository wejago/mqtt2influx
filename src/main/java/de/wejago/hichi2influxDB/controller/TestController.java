package de.wejago.hichi2influxDB.controller;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.wejago.hichi2influxDB.InfluxDBConfig;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @Autowired
    private InfluxDBConfig influxDBConfig;

    @GetMapping("/write1")
    public void testWritePoint() {
        InfluxDBClient influxDBClient = influxDBConfig.dbConnection();
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        Point point = Point.measurement("sensor").addTag("sensor_id", "TLM0102").addField("location", "Main Lobby3")
                           .addField("model_number", "TLM89092B")
                           .time(Instant.now(), WritePrecision.MS);

        writeApi.writePoint(point);
    }
}
