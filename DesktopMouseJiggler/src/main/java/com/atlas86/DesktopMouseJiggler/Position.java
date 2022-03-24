package com.atlas86.DesktopMouseJiggler;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
/**
 * 
 * @author atlas86
 */
public record Position(int x, int y) {

    private static final RandomGenerator random = RandomGeneratorFactory.getDefault().create();

    public static Position getRandomPosition() {
        int x = random.nextInt(400);
        int y = random.nextInt(400);
        return new Position(x, y);
    }
}
