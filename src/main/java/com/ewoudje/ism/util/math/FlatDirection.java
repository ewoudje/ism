package com.ewoudje.ism.util.math;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;


// Orientations, might be useable? altough for now this is fine
public enum FlatDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction moveDirection(Direction direction) {
        return switch (direction) {
            case DOWN -> switch (this) {
                case UP -> Direction.NORTH;
                case DOWN -> Direction.SOUTH;
                case LEFT -> Direction.WEST;
                case RIGHT -> Direction.EAST;
            };
            case UP -> switch (this) {
                case UP -> Direction.SOUTH;
                case DOWN -> Direction.NORTH;
                case LEFT -> Direction.WEST;
                case RIGHT -> Direction.EAST;
            };
            case NORTH -> switch (this) {
                case UP -> Direction.UP;
                case DOWN -> Direction.DOWN;
                case LEFT -> Direction.WEST;
                case RIGHT -> Direction.EAST;
            };
            case SOUTH -> switch (this) {
                case UP -> Direction.UP;
                case DOWN -> Direction.DOWN;
                case LEFT -> Direction.EAST;
                case RIGHT -> Direction.WEST;
            };
            case WEST -> switch (this) {
                case UP -> Direction.UP;
                case DOWN -> Direction.DOWN;
                case LEFT -> Direction.SOUTH;
                case RIGHT -> Direction.NORTH;
            };
            case EAST -> switch (this) {
                case UP -> Direction.UP;
                case DOWN -> Direction.DOWN;
                case LEFT -> Direction.NORTH;
                case RIGHT -> Direction.SOUTH;
            };
        };
    }

    private static final FlatDirection[][] randomisedDirections = new FlatDirection[24][4];

    static {
        int idx = 0;
        List<FlatDirection> directions = new ArrayList<>(List.of(FlatDirection.values()));
        for (int i = 0; i < 4; i++) {
            FlatDirection d1 = directions.remove(i);
            for (int j = 0; j < 3; j++) {
                FlatDirection d2 = directions.remove(j);

                for (int k = 0; k < 2; k++) {
                    FlatDirection d3 = directions.remove(k);

                    randomisedDirections[idx++] = new FlatDirection[] {
                            d1,
                            d2,
                            d3,
                            directions.getLast()
                    };

                    directions.add(d3);
                }

                directions.add(d2);
            }

            directions.add(d1);
        }
    }

    public static FlatDirection[] randomOrder(RandomSource random) {
        return randomisedDirections[random.nextInt(randomisedDirections.length)];
    }

    public boolean isHorizonal() {
        return this == LEFT  || this == RIGHT;
    }
}
