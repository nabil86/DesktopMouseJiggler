package com.atlas86.DesktopMouseJiggler;

import java.util.Objects;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
/**
 * 
 * @author atlas86
 */
public class Position {

    private int x;
    private int y;
    private static final RandomGenerator random = RandomGeneratorFactory.getDefault().create();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Position getRandomPosition() {
        int x = random.nextInt(400);
        int y = random.nextInt(400);
        return new Position(x, y);
    }

    @Override
    public String toString() {
        return "Position at x= " + x + " and y=" + y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Position other = (Position) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

}
