package de.wejago.mqtt2influx.repository;

import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.MqttDataPoint;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class KafkaRepository {
    private final KafkaTemplate<String, MqttDataPoint> kafkaTemplate;

    public void writePoint(MqttDataPoint data) {
        kafkaTemplate.send("mqtt", data.getDevice_name(), data);
    }
}
