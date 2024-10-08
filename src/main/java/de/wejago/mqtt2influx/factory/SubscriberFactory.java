package de.wejago.mqtt2influx.factory;

import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.repository.InfluxDbRepository;
import de.wejago.mqtt2influx.service.JsonSubscriber;
import de.wejago.mqtt2influx.service.RawSubscriber;
import de.wejago.mqtt2influx.service.RawSubscriberService;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriberFactory {
    public static final String TYPE_RAW = "raw";
    private final InfluxDbRepository influxDbRepository;
    private final RawSubscriberService rawSubscriberService;

    public IMqttMessageListener create(Device device) {
        if (TYPE_RAW.equals(device.getType())) {
            return new RawSubscriber(rawSubscriberService, device);
        } else {
            return new JsonSubscriber(influxDbRepository, device);
        }
    }
}
