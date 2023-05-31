package de.wejago.mqtt2influx.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.repository.InfluxDbRepository;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RawSubscriberServiceTest {
    @Mock
    private InfluxDbRepository influxDbRepository;
    @InjectMocks
    private RawSubscriberService rawSubscriberService;

    @Test
    void testUpdatePoint_WhenDeviceNotInPoints() {
        //GIVEN
        Device device = buildTestDevice();
        String key = "yieldtotal";
        Double measurement = 208.73;

        assertThat(rawSubscriberService.getPoints()).doesNotContainKeys(device.getSensorId());

        // WHEN
        rawSubscriberService.updatePoint(device, key, measurement);

        // THEN
        assertThat(rawSubscriberService.getPoints()).containsKey(device.getSensorId());

        Point testPoint = buildTestPoint();
        assertThat(rawSubscriberService.getPoints().get(device.getSensorId()))
            .usingRecursiveComparison()
            .isEqualTo(testPoint);
    }

    @Test
    void testUpdatePoint_WhenDeviceInPointsAndFieldAlreadyExists() {
        //GIVEN
        Device device = buildTestDevice();
        String key = "yieldtotal";
        Double measurement = 106.30;

        Point testPoint = buildTestPoint();
        rawSubscriberService.getPoints().put(device.getSensorId(), testPoint);
        assertThat(rawSubscriberService.getPoints()).containsKey(device.getSensorId());

        // WHEN
        rawSubscriberService.updatePoint(device, key, measurement);

        // THEN
        Point updatedPoint = Point.measurement("sensor")
                                  .addTag("sensor_id", "device_id")
                                  .addTag("device_name", "testName")
                                  .addField("Total Production", 106.30);

        assertThat(rawSubscriberService.getPoints().get(device.getSensorId()))
            .usingRecursiveComparison()
            .isEqualTo(updatedPoint);
    }

    @Test
    void testUpdatePoint_WhenDeviceInPointsAndFieldNotExists() {
        //GIVEN
        Device device = buildTestDevice();
        String key = "yieldday";
        Double measurement = 106.30;

        Point testPoint = buildTestPoint();
        rawSubscriberService.getPoints().put(device.getSensorId(), testPoint);

        assertThat(rawSubscriberService.getPoints()).containsKey(device.getSensorId());

        // WHEN
        rawSubscriberService.updatePoint(device, key, measurement);

        // THEN
        Point updatedPoint = Point.measurement("sensor")
                                  .addTag("sensor_id", "device_id")
                                  .addTag("device_name", "testName")
                                  .addField("Total Production", 208.73)
                                  .addField("Produced today", 106.30);

        assertThat(rawSubscriberService.getPoints().get(device.getSensorId()))
            .usingRecursiveComparison()
            .isEqualTo(updatedPoint);
    }

    @Test
    void scheduleAddPointToQueue() {
        Point point = buildTestPoint();
        rawSubscriberService.getPoints().put("test_key", point);
        // WHEN
        rawSubscriberService.scheduleAddPointToQueue();
        // THEN
        verify(influxDbRepository, times(1)).writePoint(point);
    }

    @Test
    void scheduleAddPointToQueue_WhenPointFieldsEmpty() {
        Point point = Point.measurement("sensor").addTag("sensor_id", "test1");
        rawSubscriberService.getPoints().put("test_key", point);
        // WHEN
        rawSubscriberService.scheduleAddPointToQueue();
        // THEN
        verify(influxDbRepository, never()).writePoint(any());
    }

    private Device buildTestDevice() {
        Map<String, String> deviceMappings = new HashMap<>();

        deviceMappings.put("yieldtotal", "Total Production");
        deviceMappings.put("yieldday", "Produced today");
        deviceMappings.put("power", "Current power");
        deviceMappings.put("powerdc", "Direct Current Power");
        deviceMappings.put("voltage", "Voltage");
        deviceMappings.put("current", "Current");
        deviceMappings.put("frequency", "Frequency");
        deviceMappings.put("powerfactor", "Power Factor");
        deviceMappings.put("efficiency", "Efficiency");
        deviceMappings.put("reactivepower", "Reactive Power");
        deviceMappings.put("temperature", "Temperature");

        Device device = new Device();
        device.setName("testName");
        device.setSensorId("device_id");
        device.setMappings(deviceMappings);

        return device;
    }


    private Point buildTestPoint() {
        return Point.measurement("sensor")
                    .addTag("sensor_id", "device_id")
                    .addTag("device_name", "testName")
                    .addField("Total Production", 208.73);
    }
}
