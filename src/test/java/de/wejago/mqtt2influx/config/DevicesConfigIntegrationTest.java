package de.wejago.mqtt2influx.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DevicesConfigIntegrationTest {

    @Autowired
    private DevicesConfig devicesConfig;

    @Test
    void testDevicesConfigLoaded() {
        List<Device> devices = devicesConfig.getDevices();
        assertNotNull(devices);
        assertEquals(2, devices.size());

        Device device1 = devices.get(0);
        assertEquals("hichi", device1.getName());
        assertEquals("json", device1.getType());
        assertEquals("tele/tasmota_FA2296/SENSOR", device1.getTopic());
        assertEquals("1_8_0", device1.getOnlyMatch());
        Map<String, String> mappings1 = device1.getMappings();
        assertEquals(9, mappings1.size());
        assertEquals("Total consumption", mappings1.get("1_8_0"));

        Device device2 = devices.get(1);
        assertEquals("tele (Other consumption monitor)", device2.getName());
        assertEquals("json", device2.getType());
        assertEquals("tele/power/SENSOR", device2.getTopic());
        assertEquals("device_id", device2.getOnlyMatch());
        Map<String, String> mappings2 = device2.getMappings();
        assertEquals(4, mappings2.size());
        assertEquals("Sum received", mappings2.get("Total_in"));
    }
}
