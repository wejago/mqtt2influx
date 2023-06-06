package de.wejago.mqtt2influx.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.influxdb.client.write.Point;
import de.wejago.mqtt2influx.config.Device;
import de.wejago.mqtt2influx.config.MqttDataPoint;
import de.wejago.mqtt2influx.repository.InfluxDbRepository;
import de.wejago.mqtt2influx.repository.KafkaRepository;
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
    private KafkaRepository kafkaRepository;
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

        MqttDataPoint testPoint = buildTestMqttPoint();
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

        MqttDataPoint testMqttPoint = buildTestMqttPoint();
        rawSubscriberService.getPoints().put(device.getSensorId(), testMqttPoint);
        assertThat(rawSubscriberService.getPoints()).containsKey(device.getSensorId());

        // WHEN
        rawSubscriberService.updatePoint(device, key, measurement);

        // THEN
        MqttDataPoint mqttUpdatedPoint = buildUpdatedMqttPoint();

        assertThat(rawSubscriberService.getPoints().get(device.getSensorId()))
            .usingRecursiveComparison()
            .isEqualTo(mqttUpdatedPoint);
    }

//    @Test
//    void testUpdatePoint_WhenDeviceInPointsAndFieldNotExists() {
//        //GIVEN
//        Device device = buildTestDevice();
//        String key = "yieldday";
//        Double measurement = 106.30;
//
//        MqttDataPoint testMqttPoint = buildTestMqttPoint();
//        rawSubscriberService.getPoints().put(device.getSensorId(), testMqttPoint);
//
//        assertThat(rawSubscriberService.getPoints()).containsKey(device.getSensorId());
//
//        // WHEN
//        rawSubscriberService.updatePoint(device, key, measurement);
//
//        // THEN
//        Point updatedPoint = Point.measurement("sensor")
//                                  .addTag("sensor_id", "device_id")
//                                  .addTag("device_name", "testName")
//                                  .addField("Total Production", 208.73)
//                                  .addField("Produced today", 106.30);
//
//        assertThat(rawSubscriberService.getPoints().get(device.getSensorId()))
//            .usingRecursiveComparison()
//            .isEqualTo(updatedPoint);
//    }

    @Test
    void scheduleAddPointToQueue() {
        MqttDataPoint mqttTestPoint = buildTestMqttPoint();
        rawSubscriberService.getPoints().put("test_key", mqttTestPoint);
        // WHEN
        rawSubscriberService.scheduleAddPointToQueue();
        // THEN
        verify(kafkaRepository, times(1)).writePoint(mqttTestPoint);
    }

    @Test
    void scheduleAddPointToQueue_WhenPointFieldsEmpty() {
        MqttDataPoint mqttTestPoint = new MqttDataPoint();
        mqttTestPoint.setSensor_id("test1");
        rawSubscriberService.getPoints().put("test_key", mqttTestPoint);
        // WHEN
        rawSubscriberService.scheduleAddPointToQueue();
        // THEN
        verify(kafkaRepository, never()).writePoint(any());
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


    private MqttDataPoint buildTestMqttPoint() {
        MqttDataPoint testMqttPoint = new MqttDataPoint();
        testMqttPoint.setSensor_id("device_id");
        testMqttPoint.setDevice_name("testName");
        Map<String, Object> fields = new HashMap<>();
        fields.put("Total Production", 208.73);
        testMqttPoint.setFields(fields);
        return testMqttPoint;
    }

    private MqttDataPoint buildUpdatedMqttPoint() {
        MqttDataPoint mqttUpdatedPoint = new MqttDataPoint();
        mqttUpdatedPoint.setSensor_id("device_id");
        mqttUpdatedPoint.setDevice_name("testName");
        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("Total Production", 106.30);
        mqttUpdatedPoint.setFields(updatedFields);
        return mqttUpdatedPoint;
    }
}
