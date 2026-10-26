package de.wejago.mqtt2influx.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DevicesConfigIntegrationTest {

    @Autowired
    private DevicesConfig devicesConfig;

    @Test
    void testDevicesConfigLoaded() {
        List<Device> devices = devicesConfig.getDevices();
        assertThat(devices).isNotNull().hasSize(3);

        checkDevice(devices.get(0), "hichi", "json", "tele/tasmota_FA2296/SENSOR", "1_8_0",
                9, "1_8_0", "Total Consumption");

        assertThat(devices.get(0).getLimits()).extracting(ValueLimit::getField)
                .contains("Total Consumption", "Current Consumption");
        assertThat(devices.get(0).isPlausible("Current Consumption", 999999D)).isFalse();
        assertThat(devices.get(0).isPlausible("Total Consumption", 0D)).isFalse();
        assertThat(devices.get(0).isPlausible("Total Consumption", 44550.5D)).isTrue();

        checkDevice(devices.get(1), "tele (Other consumption monitor)", "json", "tele/power/SENSOR", "device_id",
                4, "Total_in", "Total Consumption");

        checkDevice(devices.get(2), "solar", "raw", "solar/111ThisShouldBeChanged111/0/#", null,
                11, "yieldtotal", "Total Production");
    }

    private static void checkDevice(final Device device, String name, String type, String topic, String onlyMatch,
                                    int mappings, String key, String value) {
        assertThat(device.getName()).isEqualTo(name);
        assertThat(device.getType()).isEqualTo(type);
        assertThat(device.getTopic()).isEqualTo(topic);
        if(onlyMatch != null) {
            assertThat(device.getOnlyMatch()).isEqualTo(onlyMatch);
        }
        assertThat(device.getMappings()).hasSize(mappings).containsEntry(key, value);
    }
}
