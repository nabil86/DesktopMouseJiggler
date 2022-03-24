package com.atlas86.DesktopMouseJiggler;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atlas86
 */
public class Mouse implements Runnable{

    public static final long WAIT = 10000L;
    private Robot robot;
    private Position oldPosition;
    private AtomicBoolean running = new AtomicBoolean(false);
    private Thread mouseThread;
    
    public Mouse() {
        try {
            robot = new Robot();
        } catch (Exception e) {
            System.err.print(e);
        }
    }
    private boolean hasNotMovedByUser(Position newPosition) {
        if (oldPosition == null) {
            return true;
        }
        var currentPosition = getCurrentPointerPosition();
        return Objects.equals(oldPosition, currentPosition);
    }

    private Position getCurrentPointerPosition() {
        var pointerInfo = MouseInfo.getPointerInfo();
        var location = pointerInfo.getLocation();
        int x = (int) location.getX();
        int y = (int) location.getY();
        return new Position(x, y);
    }

    public void start(){
        mouseThread = new Thread(this);
        mouseThread.start();
    }
    
    public void stop(){
        this.running.set(false);
    }
    
    boolean isRunning() {
        return running.get();
    }
      
    private void move() throws InterruptedException {
        if (hasNotMovedByUser(oldPosition)) {
            var position = Position.getRandomPosition();
            System.out.println("Mouse move to " + position);
            robot.mouseMove(position.x(), position.y());
            oldPosition = position;
            Thread.sleep(WAIT);
        } else {
            oldPosition = getCurrentPointerPosition();
            System.out.println("User has moved mouse, wait next iteration");
            Thread.sleep(WAIT * 3);
        }
    }

    @Override
    public void run() {
        this.running.set(true);
        while (running.get()) {
            try {
                move();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                Logger.getLogger(Mouse.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
}
