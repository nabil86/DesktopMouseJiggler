package com.atlas86.DesktopMouseJiggler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @ParameterizedTest(name = "{0} is active={2}")
    @CsvSource({
        "08:00, true",
        "10:30, true",
        "11:59, true",
        "12:00, false",
        "13:00, false",
        "14:00, true",
        "17:59, true",
        "18:00, false",
        "23:00, false"
    })
    void isWithinActiveWindow_defaultConfig(String timeStr, boolean expected) {
        var config = AppConfig.defaultConfig();
        var time   = LocalTime.parse(timeStr);
        assertEquals(expected, config.isWithinActiveWindow(time));
    }

    @Test
    void defaultConfig_hasExpectedValues() {
        var config = AppConfig.defaultConfig();
        assertEquals(10L, config.intervalSec());
        assertEquals(400, config.amplitudePx());
        assertEquals(LocalTime.of(8,  0), config.morningStart());
        assertEquals(LocalTime.of(12, 0), config.morningEnd());
        assertEquals(LocalTime.of(14, 0), config.afternoonStart());
        assertEquals(LocalTime.of(18, 0), config.afternoonEnd());
    }
}
