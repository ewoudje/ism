package com.ewoudje.ism.util.math;

import com.mojang.math.OctahedralGroup;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.core.Direction;

public class RotationDirectionUtil {
    public static OctahedralGroup direction2octahedral(Direction direction) {
        return switch (direction) {
            case DOWN -> OctahedralGroup.BLOCK_ROT_X_90;
            case UP -> OctahedralGroup.BLOCK_ROT_X_270;
            case NORTH -> OctahedralGroup.IDENTITY;
            case SOUTH -> OctahedralGroup.BLOCK_ROT_Y_180;
            case WEST -> OctahedralGroup.BLOCK_ROT_Y_270;
            case EAST -> OctahedralGroup.BLOCK_ROT_Y_90;
        };
    }

    public static BlockModelRotation direction2bmRotation(Direction direction) {
        return BlockModelRotation.get(direction2octahedral(direction));
    }
}
