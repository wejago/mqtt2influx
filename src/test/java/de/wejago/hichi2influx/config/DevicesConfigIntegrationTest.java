package de.wejago.hichi2influx.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DevicesConfigIntegrationTest {

    @Autowired
    private DevicesConfig devicesConfig;

    @Test
    public void testDevicesConfigLoaded() {
        List<Device> devices = devicesConfig.getDevices();
        assertNotNull(devices);
        assertEquals(2, devices.size());

        Device device1 = devices.get(0);
        assertEquals("hichi", device1.getName());
        assertEquals("json", device1.getType());
        assertEquals("tele/tasmota_FA2296/SENSOR", device1.getTopic());
        assertEquals("1_8_0", device1.getOnly_match());
        Map<String, String> mappings1 = device1.getMappings();
        assertEquals(9, mappings1.size());
        assertEquals("Total consumption", mappings1.get("0.1_8_0"));

        Device device2 = devices.get(1);
        assertEquals("tele (parents Jens)", device2.getName());
        assertEquals("json", device2.getType());
        assertEquals("tele/power/SENSOR", device2.getTopic());
        assertEquals("device_id", device2.getOnly_match());
        Map<String, String> mappings2 = device2.getMappings();
        assertEquals(3, mappings2.size());
        assertEquals("Sum received", mappings2.get("0.Total_in"));
    }
}
