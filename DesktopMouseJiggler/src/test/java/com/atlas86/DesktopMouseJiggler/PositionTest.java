package com.atlas86.DesktopMouseJiggler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void getRandomPosition_returnsValuesWithinBounds() {
        int amplitude = 200;
        for (int i = 0; i < 100; i++) {
            var pos = Position.getRandomPosition(amplitude);
            assertTrue(pos.x() >= 0 && pos.x() < amplitude,
                    "x=" + pos.x() + " must be in [0, " + amplitude + ")");
            assertTrue(pos.y() >= 0 && pos.y() < amplitude,
                    "y=" + pos.y() + " must be in [0, " + amplitude + ")");
        }
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "100, 200", "-5, 10"})
    void record_storesExactValues(int x, int y) {
        var pos = new Position(x, y);
        assertEquals(x, pos.x());
        assertEquals(y, pos.y());
    }

    @Test
    void equals_sameCoordinates_returnsTrue() {
        assertEquals(new Position(10, 20), new Position(10, 20));
    }

    @Test
    void equals_differentCoordinates_returnsFalse() {
        assertNotEquals(new Position(10, 20), new Position(10, 21));
    }
}
