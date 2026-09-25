package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.util.math.FlatDirection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.math.OctahedralGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class TristitiaClingyGrowthBlock extends TristitiaGrowthBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(
            Maps.newEnumMap(
                    Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST, Direction.UP, UP, Direction.DOWN, DOWN)
            )
    );

    private final Function<BlockState, VoxelShape> shapes;
    private final float thickness;

    public TristitiaClingyGrowthBlock(Properties properties, float thickness) {
        super(properties);
        this.thickness = thickness;

        Map<Direction, VoxelShape> shapes = Shapes.rotateAll(Block.boxZ(16.0, 0.0, thickness));
        this.shapes = getShapeForEachState(state -> {
            VoxelShape shape = Shapes.empty();

            for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
                if (state.getValue(entry.getValue())) {
                    shape = Shapes.or(shape, shapes.get(entry.getKey()));
                }
            }

            return shape.isEmpty() ? Shapes.block() : shape;
        });

        var defaultBlockState = getStateDefinition().any();
        for (BooleanProperty p : PROPERTY_BY_DIRECTION.values()) {
            defaultBlockState = defaultBlockState.setValue(p, false);
        }
        registerDefaultState(defaultBlockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getExpensiveShape(level, pos, state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.apply(state);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.apply(state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        BlockState result = defaultBlockState();

        for (var entry : PROPERTY_BY_DIRECTION.entrySet()) {
            if (state.getValue(entry.getValue())) {
                state.setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(entry.getKey())), true);
            }
        }

        return result;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState result = defaultBlockState();

        for (var entry : PROPERTY_BY_DIRECTION.entrySet()) {
            if (state.getValue(entry.getValue())) {
                state.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(entry.getKey())), true);
            }
        }

        return result;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return anyFaces(getUpdatedState(level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState result = getUpdatedState(context.getLevel(), context.getClickedPos());
        return anyFaces(result) ? result : null;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction directionToNeighbour,
            BlockPos neighbourPos,
            BlockState neighbourState,
            RandomSource random
    ) {
        BlockState blockState = getUpdatedState(level, pos);
        return anyFaces(blockState) ? blockState : Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState getStateForGrowth(ServerLevel level, BlockPos pos, RandomSource random) {
        return getUpdatedState(level, pos);
    }

    private VoxelShape getExpensiveShape(BlockGetter level, BlockPos pos, BlockState state) {
        return getFaces(state).map(dir -> {
            double xMin = 0, yMin = 0, xMax = 0, yMax = 0;

            for (FlatDirection side : FlatDirection.values()) {
                double value = 14.0;
                if (state.getValue(PROPERTY_BY_DIRECTION.get(side.moveDirection(dir)))
                        || isFaceFlatConnected(level, pos, dir, side)) {
                    value = 16.0;
                } else if (isFaceCornerConnected(level, pos, dir, side, thickness)) {
                    value = 16.0 + thickness;
                }

                switch (side) {
                    case UP -> yMax = value;
                    case DOWN -> yMin = 16.0 - value;
                    case LEFT -> xMin = 16.0 - value;
                    case RIGHT -> xMax = value;
                }
            }

            return Shapes.rotate(
                    Block.box(
                            xMin, yMin, 0,
                            xMax, yMax, thickness
                    ),
                    switch (dir) {
                        case NORTH -> OctahedralGroup.IDENTITY;
                        case EAST -> OctahedralGroup.BLOCK_ROT_Y_90;
                        case SOUTH -> OctahedralGroup.BLOCK_ROT_Y_180;
                        case WEST -> OctahedralGroup.BLOCK_ROT_Y_270;
                        case UP -> OctahedralGroup.BLOCK_ROT_X_270;
                        case DOWN -> OctahedralGroup.BLOCK_ROT_X_90;
                    }
            );
        }).reduce(Shapes.empty(), Shapes::or);
    }

    private BlockState getUpdatedState(BlockGetter level, BlockPos pos) {
        BlockState state = defaultBlockState();

        for (var pair : PROPERTY_BY_DIRECTION.entrySet()) {
            state = state.setValue(pair.getValue(), MultifaceBlock.canAttachTo(level, pos, pair.getKey()));
        }

        return state;
    }

    public boolean anyFaces(BlockState state) {
        return getFaces(state).findAny().isPresent();
    }

    public Stream<Direction> getFaces(BlockState state) {
        return PROPERTY_BY_DIRECTION.entrySet().stream()
                .filter(entry -> state.getValue(entry.getValue()))
                .map(Map.Entry::getKey);
    }

    public float thickness() {
        return thickness;
    }

    public static boolean isFaceFlatConnected(BlockGetter level, BlockPos pos, Direction face, FlatDirection direction) {
        Direction combinedDir = direction.moveDirection(face);
        BlockPos step = pos.relative(combinedDir);
        BlockState blockState = level.getBlockState(step);

        if (blockState.getBlock() instanceof TristitiaClingyGrowthBlock) {
            return blockState.getValue(PROPERTY_BY_DIRECTION.get(face));
        } else return false;
    }

    public static boolean isFaceCornerConnected(BlockGetter level, BlockPos pos, Direction face, FlatDirection direction, float thickness) {
        Direction combinedDir = direction.moveDirection(face);
        BlockPos step = pos.relative(combinedDir);
        BlockState blockState = level.getBlockState(step.relative(face));

        if (blockState.getBlock() instanceof TristitiaClingyGrowthBlock b) {
            return b.thickness >= thickness && blockState.getValue(PROPERTY_BY_DIRECTION.get(combinedDir.getOpposite()));
        } else return false;
    }
}
