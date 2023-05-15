package de.wejago.mqtt2influx.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.repository.InfluxDbRepository;
import de.wejago.mqtt2influx.service.JsonSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JsonSubscriberFactory {
    private final ObjectMapper objectMapper;
    private final InfluxDbRepository influxDbRepository;

    public JsonSubscriber create(Device device) {
        return new JsonSubscriber(objectMapper, influxDbRepository, device);
    }
}
