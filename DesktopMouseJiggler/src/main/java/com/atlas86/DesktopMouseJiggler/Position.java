package com.atlas86.DesktopMouseJiggler;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Immutable (x, y) screen position.
 */
public record Position(int x, int y) {

    /**
     * Returns a random position within [0, amplitudePx) on each axis.
     */
    public static Position getRandomPosition(int amplitudePx) {
        var rng = ThreadLocalRandom.current();
        return new Position(rng.nextInt(amplitudePx), rng.nextInt(amplitudePx));
    }

    
}
