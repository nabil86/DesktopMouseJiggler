package com.atlas86.DesktopMouseJiggler;

import org.junit.jupiter.api.Test;

import java.awt.MouseInfo;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MouseTest {

    private AtomicReference<AppConfig> defaultConfigRef() {
        return new AtomicReference<>(AppConfig.defaultConfig());
    }

    @Test
    void initialState_isNotRunning() {
        var mouse = new Mouse(defaultConfigRef());
        assertFalse(mouse.isRunning());
    }

    @Test
    void start_setsRunningTrue() throws InterruptedException {
        var mouse = new Mouse(defaultConfigRef());
        mouse.start();
        try {
            assertTrue(mouse.isRunning());
        } finally {
            mouse.stop();
        }
    }

    @Test
    void stop_setsRunningFalse() throws InterruptedException {
        var mouse = new Mouse(defaultConfigRef());
        mouse.start();
        mouse.stop();
        // Give the thread a moment to observe the flag
        Thread.sleep(200);
        assertFalse(mouse.isRunning());
    }

    @Test
    void start_calledTwice_doesNotLeakThread() {
        var mouse = new Mouse(defaultConfigRef());
        mouse.start();
        mouse.start(); // second call must be a no-op
        assertTrue(mouse.isRunning());
        mouse.stop();
    }

    @Test
    void hasNotMovedByUser_oldPositionNull_returnsTrue() {
        var mouse = new Mouse(defaultConfigRef());
        // oldPosition is initially null
        assertTrue(mouse.hasNotMovedByUser(), "Should return true when oldPosition is null");
    }

    @Test
    void hasNotMovedByUser_oldPositionMatchesCurrent_returnsTrue() {
        var mouse = new Mouse(defaultConfigRef());
        
        // Capture la position courante de la souris (environnement réel)
        var location = MouseInfo.getPointerInfo().getLocation();
        var currentPos = new Position((int) location.getX(), (int) location.getY());
        
        // Simule que le jiggler a placé la souris à cet endroit
        mouse.setOldPosition(currentPos);
        
        assertTrue(mouse.hasNotMovedByUser(), "Should return true when oldPosition matches current system pointer");
    }

    @Test
    void hasNotMovedByUser_oldPositionDifferentFromCurrent_returnsFalse() {
        var mouse = new Mouse(defaultConfigRef());
        
        // Capture la position courante et ajoute un offset artificiel
        var location = MouseInfo.getPointerInfo().getLocation();
        var fakeOldPos = new Position((int) location.getX() + 10, (int) location.getY() + 10);
        
        // Simule que le jiggler avait placé la souris avec cet offset
        mouse.setOldPosition(fakeOldPos);
        
        // Puisque la position système réelle courante est différente de fakeOldPos, ça doit retourner false
        assertFalse(mouse.hasNotMovedByUser(), "Should return false when user has moved the mouse away from oldPosition");
    }
}
