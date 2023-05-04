package de.wejago.hichi2influx.factory;

import de.wejago.hichi2influx.config.Device;
import de.wejago.hichi2influx.service.JsonSubscriber;

public interface JsonSubscriberFactory {
    JsonSubscriber create(Device device);
}
