package de.wejago.hichi2influxDB.controller;

import java.sql.SQLOutput;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("/db-connection")
    public void dbConnect() {
        String databaseURL = "localhost:8086/bucket1";
        String userName = "wejago";
        String password = "wejagoParola";
        InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);

        Pong response = influxDB.ping();
        if (response.getVersion().equalsIgnoreCase("unknown")) {
            System.out.println("Error pinging server.");
            return;
        } else {
            System.out.println("ping was successful!");
        }

    }
}
