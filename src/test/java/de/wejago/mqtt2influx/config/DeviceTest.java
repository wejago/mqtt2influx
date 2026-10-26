package de.wejago.mqtt2influx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceTest {
    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device();
        device.setLimits(List.of(
                limit("Current Consumption", -25000D, 25000D),
                limit("Total Consumption", 1D, null)));
    }

    @Test
    void isPlausible_valueInsideLimits() {
        assertThat(device.isPlausible("Current Consumption", 1151D)).isTrue();
        assertThat(device.isPlausible("Current Consumption", -9.59D)).isTrue();
    }

    @Test
    void isPlausible_valueOnBoundary() {
        assertThat(device.isPlausible("Current Consumption", 25000D)).isTrue();
        assertThat(device.isPlausible("Current Consumption", -25000D)).isTrue();
    }

    @Test
    void isPlausible_valueOutsideLimits() {
        assertThat(device.isPlausible("Current Consumption", 999999D)).isFalse();
        assertThat(device.isPlausible("Current Consumption", 40676233)).isFalse();
        assertThat(device.isPlausible("Current Consumption", -25000.01D)).isFalse();
    }

    @Test
    void isPlausible_onlyMinConfigured() {
        assertThat(device.isPlausible("Total Consumption", 0D)).isFalse();
        assertThat(device.isPlausible("Total Consumption", 0.01D)).isFalse();
        assertThat(device.isPlausible("Total Consumption", 44550.5D)).isTrue();
        assertThat(device.isPlausible("Total Consumption", 2919543519.14D)).isTrue();
    }

    @Test
    void isPlausible_fieldWithoutLimit() {
        assertThat(device.isPlausible("Tariff 1 Consumption", 999999D)).isTrue();
    }

    @Test
    void isPlausible_nonNumericValue() {
        assertThat(device.isPlausible("Current Consumption", "012345affecaffee")).isTrue();
    }

    @Test
    void isPlausible_noLimitsConfigured() {
        device.setLimits(null);

        assertThat(device.isPlausible("Current Consumption", 999999D)).isTrue();
    }

    private static ValueLimit limit(String field, Double min, Double max) {
        final ValueLimit limit = new ValueLimit();
        limit.setField(field);
        limit.setMin(min);
        limit.setMax(max);
        return limit;
    }
}
