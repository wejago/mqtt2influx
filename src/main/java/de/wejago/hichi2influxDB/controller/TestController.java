package de.wejago.hichi2influxDB.controller;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("/db-test")
    public void dbConnectNew() {
        InfluxDBClient influxDBClient = this.buildConnection("http://localhost:8086", "9UBCCJiu9Tn3xdtUqriXfYK_WFmSaT8yy21QyDdpJ4-hE8yoOX3m9s78WYN_P6u0vvtbjuVDt90sJpm04lmKdA==", "bucket1", "wejago");
        System.out.println(influxDBClient);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        Point point = Point.measurement("sensor").addTag("sensor_id", "TLM0100").addField("location", "Main Lobby")
                           .addField("model_number", "TLM89092A")
                           .time(Instant.now(), WritePrecision.MS);

        writeApi.writePoint(point);
    }



    private InfluxDBClient buildConnection(String url, String token, String bucket, String org) {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }
}
