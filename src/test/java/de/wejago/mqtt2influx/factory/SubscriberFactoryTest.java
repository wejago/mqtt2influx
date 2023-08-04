package de.wejago.mqtt2influx.factory;

import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.service.JsonSubscriber;
import de.wejago.mqtt2influx.service.RawSubscriber;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;


@ExtendWith(MockitoExtension.class)
class SubscriberFactoryTest {
    @InjectMocks
    private SubscriberFactory subscriberFactory;


    @Test
    void create_WithRawDevice() {
        // GIVEN
        Device rawDevice = new Device();
        rawDevice.setType("raw");

        // WHEN
        IMqttMessageListener listener = subscriberFactory.create(rawDevice);

        // THEN
        assertThat(listener, instanceOf(RawSubscriber.class));
    }

    @Test
    void create_WithJsonDevice() {
        // GIVEN
        Device jsonDevice = new Device();
        jsonDevice.setType("json");

        // WHEN
        IMqttMessageListener listener = subscriberFactory.create(jsonDevice);

        // THEN
        assertThat(listener, instanceOf(JsonSubscriber.class));
    }
}
