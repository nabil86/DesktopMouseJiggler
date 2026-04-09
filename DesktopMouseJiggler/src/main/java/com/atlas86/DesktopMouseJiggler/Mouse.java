package com.atlas86.DesktopMouseJiggler;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls mouse movement using {@link Robot}.
 * Respects the activation time windows defined in {@link AppConfig}.
 */
public class Mouse implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Mouse.class.getName());

    private final Robot robot;
    private final AtomicReference<AppConfig> configRef;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile Thread mouseThread;
    private Position oldPosition;

    public Mouse(AtomicReference<AppConfig> configRef) {
        this.configRef = configRef;
        Robot r = null;
        try {
            r = new Robot();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create Robot", e);
        }
        this.robot = r;
    }

    /** Starts the jiggler thread. No-op if already running. */
    public void start() {
        if (running.compareAndSet(false, true)) {
            mouseThread = new Thread(this, "mouse-jiggler");
            mouseThread.setDaemon(true);
            mouseThread.start();
        }
    }

    /** Signals the jiggler thread to stop. */
    public void stop() {
        running.set(false);
        var t = mouseThread;
        if (t != null) {
            t.interrupt();
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    // Rendu package-private (visibilité par défaut) pour les tests unitaires
    boolean hasNotMovedByUser() {
        if (oldPosition == null) {
            return true;
        }
        var currentLocation = getCurrentPointerPosition();
        System.out.println("Old mouse position: " + oldPosition);
        System.out.println("Current mouse position: " + currentLocation);
        return Objects.equals(oldPosition, currentLocation);
    }

    // Visible for testing
    void setOldPosition(Position position) {
        this.oldPosition = position;
    }

    private Position getCurrentPointerPosition() {
        var pointerInfo = MouseInfo.getPointerInfo();
        var location = pointerInfo.getLocation();
      
        return new Position((int) location.getX(), (int) location.getY());
    }

    private boolean isCommunicationAppRunning(String monitoredProcessesStr) {
        if (monitoredProcessesStr == null || monitoredProcessesStr.isBlank()) {
            return true; // No processes to monitor; default to returning true to keep jiggling
        }

        var targets = java.util.Arrays.stream(monitoredProcessesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .toList();

        if (targets.isEmpty()) {
            return true;
        }

        return ProcessHandle.allProcesses()
                .map(ProcessHandle::info)
                .map(ProcessHandle.Info::command)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(String::toLowerCase)
                .anyMatch(cmd -> targets.stream().anyMatch(cmd::contains));
    }

    private void move() throws InterruptedException {
        var config = configRef.get();
        long intervalSec = config.intervalSec();

        if (!config.isWithinActiveWindow(LocalTime.now())) {
            LOGGER.fine("Outside active window, skipping movement");
            TimeUnit.SECONDS.sleep(intervalSec);
            return;
        }

        if (!isCommunicationAppRunning(config.monitoredProcesses())) {
            LOGGER.fine("Monitored apps are not running. Skipping movement.");
            System.out.println("Monitored apps are not running. Skipping movement.");
            TimeUnit.SECONDS.sleep(intervalSec);
            return;
        }

        if (hasNotMovedByUser()) {
            var position = Position.getRandomPosition(config.amplitudePx());
            LOGGER.fine(() -> "Mouse move to " + position);
            System.out.println("Mouse move to " + position);
            if (robot != null) {
                robot.mouseMove(position.x(), position.y());
            }
            System.out.println("===> before setting oldPosition "+oldPosition);
            oldPosition = position;
            
            TimeUnit.SECONDS.sleep(intervalSec);
        } else {
            oldPosition = getCurrentPointerPosition();
            LOGGER.fine("User moved mouse, waiting extra interval");
            System.out.println("User moved mouse, waiting extra interval");
            TimeUnit.SECONDS.sleep(intervalSec * 3);
        }
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                move();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOGGER.info("Jiggler thread stopped.");
            }
        }
    }
}
