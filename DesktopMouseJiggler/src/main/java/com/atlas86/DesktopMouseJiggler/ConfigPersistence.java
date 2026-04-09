package com.atlas86.DesktopMouseJiggler;

import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Loads and saves {@link AppConfig} using the standard {@link Preferences} API.
 * Preferences are stored per-user by the OS (registry on Windows, XML on Linux/macOS).
 * Node: /com/atlas86/DesktopMouseJiggler
 */
public final class ConfigPersistence {

    private static final Logger LOGGER = Logger.getLogger(ConfigPersistence.class.getName());
    private static final Preferences PREFS =
            Preferences.userNodeForPackage(ConfigPersistence.class);

    private static final String KEY_INTERVAL_MS      = "intervalSec";
    private static final String KEY_AMPLITUDE_PX     = "amplitudePx";
    private static final String KEY_MORNING_START    = "morningStart";
    private static final String KEY_MORNING_END      = "morningEnd";
    private static final String KEY_AFTERNOON_START  = "afternoonStart";
    private static final String KEY_AFTERNOON_END    = "afternoonEnd";
    private static final String KEY_MONITORED_PROCESSES = "monitoredProcesses";

    private ConfigPersistence() {
    }

    /** Loads config from the OS preference store, or returns defaults if nothing is saved. */
    public static AppConfig load() {
        var defaults = AppConfig.defaultConfig();
        try {
            long      intervalMs      = PREFS.getLong(KEY_INTERVAL_MS,     defaults.intervalSec());
            int       amplitudePx     = PREFS.getInt(KEY_AMPLITUDE_PX,     defaults.amplitudePx());
            LocalTime morningStart    = LocalTime.parse(PREFS.get(KEY_MORNING_START,   defaults.morningStart().toString()));
            LocalTime morningEnd      = LocalTime.parse(PREFS.get(KEY_MORNING_END,     defaults.morningEnd().toString()));
            LocalTime afternoonStart  = LocalTime.parse(PREFS.get(KEY_AFTERNOON_START, defaults.afternoonStart().toString()));
            LocalTime afternoonEnd    = LocalTime.parse(PREFS.get(KEY_AFTERNOON_END,   defaults.afternoonEnd().toString()));
            String    monitoredProcesses = PREFS.get(KEY_MONITORED_PROCESSES, defaults.monitoredProcesses());
            return new AppConfig(intervalMs, amplitudePx, morningStart, morningEnd, afternoonStart, afternoonEnd, monitoredProcesses);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Malformed preferences, using defaults", e);
            return defaults;
        }
    }

    /** Persists the given config to the OS preference store. */
    public static void save(AppConfig config) {
        PREFS.putLong(KEY_INTERVAL_MS,     config.intervalSec());
        PREFS.putInt(KEY_AMPLITUDE_PX,     config.amplitudePx());
        PREFS.put(KEY_MORNING_START,       config.morningStart().toString());
        PREFS.put(KEY_MORNING_END,         config.morningEnd().toString());
        PREFS.put(KEY_AFTERNOON_START,     config.afternoonStart().toString());
        PREFS.put(KEY_AFTERNOON_END,       config.afternoonEnd().toString());
        PREFS.put(KEY_MONITORED_PROCESSES, config.monitoredProcesses());
        try {
            PREFS.flush();
        } catch (BackingStoreException e) {
            LOGGER.log(Level.WARNING, "Failed to flush preferences to disk", e);
        }
    }
}
