package de.wejago.hichi2influxDB.service;

import de.wejago.hichi2influxDB.dto.SensorEntry;
import de.wejago.hichi2influxDB.model.SensorData;

public class Mqtt2InfluxDbService {
    //used this logic as template https://github.com/iothon/docker-compose-mqtt-influxdb-grafana/blob/master/02-bridge/main.py

    public Boolean onConnect() {
        //The callback for when the client receives a CONNACK response from the server.
        return true;
    }

    public Boolean onMessage() {
        //The callback for when a PUBLISH message is received from the server.
        return true;
    }

    private SensorEntry parseMqttMessage() {
        return new SensorEntry();
    }

    private SensorData saveDataToDb(SensorEntry sensorEntry) {
        SensorData sensorData = new SensorData();
        return sensorData;
    }
}
