package de.wejago.hichi2influx.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wejago.hichi2influx.config.Device;
import de.wejago.hichi2influx.repository.InfluxDbRepository;
import de.wejago.hichi2influx.service.JsonSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonSubscriberFactoryImpl implements JsonSubscriberFactory{
    private final ObjectMapper objectMapper;
    private final InfluxDbRepository influxDbRepository;

    @Autowired
    public JsonSubscriberFactoryImpl(ObjectMapper objectMapper, InfluxDbRepository influxDbRepository) {
        this.objectMapper = objectMapper;
        this.influxDbRepository = influxDbRepository;
    }

    @Override
    public JsonSubscriber create(Device device) {
        return new JsonSubscriber(objectMapper, influxDbRepository, device);
    }
}
