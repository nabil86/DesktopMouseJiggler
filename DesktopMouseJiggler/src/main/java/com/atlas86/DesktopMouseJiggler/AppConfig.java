package com.atlas86.DesktopMouseJiggler;

import java.time.LocalTime;

/**
 * Immutable configuration for the mouse jiggler.
 *
 * @param intervalSec      Delay between mouse movements in seconds (default 10).
 * @param amplitudePx      Maximum pixel offset for random movements (default 400).
 * @param morningStart     Start of the morning activation window (default 08:00).
 * @param morningEnd       End of the morning activation window (default 12:00).
 * @param afternoonStart   Start of the afternoon activation window (default 14:00).
 * @param afternoonEnd     End of the afternoon activation window (default 18:00).
 */
public record AppConfig(
        long intervalSec,
        int amplitudePx,
        LocalTime morningStart,
        LocalTime morningEnd,
        LocalTime afternoonStart,
        LocalTime afternoonEnd,
        String monitoredProcesses
) {

    /** Default configuration. */
    public static AppConfig defaultConfig() {
        return new AppConfig(
                10L,
                400,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0),
                LocalTime.of(18, 0),
                "teams,slack"
        );
    }

    /**
     * Returns true if the given time falls within the morning or afternoon activation window.
     */
    public boolean isWithinActiveWindow(LocalTime time) {
        return isBetween(time, morningStart, morningEnd)
                || isBetween(time, afternoonStart, afternoonEnd);
    }

    private static boolean isBetween(LocalTime time, LocalTime start, LocalTime end) {
        return !time.isBefore(start) && time.isBefore(end);
    }
}
