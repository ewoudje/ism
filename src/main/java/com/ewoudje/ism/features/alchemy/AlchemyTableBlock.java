package com.ewoudje.ism.features.alchemy;

import com.ewoudje.ism.collections.IsmDataComponents;
import com.ewoudje.ism.features.notebook.NotebookData;
import com.ewoudje.ism.util.block.BlockWithEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class AlchemyTableBlock extends Block implements BlockWithEntity<AlchemyTableBlockEntity> {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final Map<Direction, VoxelShape> UPPER_SHAPE = Shapes.rotateAll(
            box(0.0, -1.0, 15.0, 16.0, 16.0, 16.0)
    );
    public static final Map<Direction, VoxelShape> LOWER_SHAPE = Shapes.rotateAll(Shapes.or(
            box(1.0, 2.0, 2.0, 15.0, 15.0, 16.0),
            box(0.0, 14.0, 0.0, 16.0, 15.0, 16.0)
    ));
    private static final double BASE_Y_HIT_DIFF = 14.0 / 16.0;

    public AlchemyTableBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) return InteractionResult.PASS;

        Vec3 hitLocation = hitResult.getLocation();
        if (hitLocation.y - pos.getY() < BASE_Y_HIT_DIFF) {

            if (!level.isClientSide()) {
                MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
                if (menuProvider != null) {
                    player.openMenu(menuProvider);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean isUpper = state.getValue(HALF) == DoubleBlockHalf.UPPER;
        boolean isAboveBoard = isUpper || hitResult.getLocation().y - pos.getY() > BASE_Y_HIT_DIFF;

        if (isAboveBoard) {
            var be = isUpper ? getBlockEntity(level, pos.below()) : getBlockEntity(level, pos);
            if (itemStack.has(IsmDataComponents.NOTEBOOK)) {
                if (!level.isClientSide()) {
                    be.setItem(
                            AlchemyTableBlockEntity.NOTEBOOK_SLOT,
                            itemStack.split(1)
                    );
                }

                return InteractionResult.SUCCESS;
            }

            ItemStack notebook = be.getItem(AlchemyTableBlockEntity.NOTEBOOK_SLOT);
            if (notebook.isEmpty()) return InteractionResult.PASS;
            NotebookData notebookData = notebook.get(IsmDataComponents.NOTEBOOK);
            if (notebookData == null) return InteractionResult.PASS; // Should never happen

            if (player.isCrouching()) {
                if (itemStack.isEmpty()) {
                    if (!level.isClientSide()) {
                        player.setItemInHand(hand, be.getItem(AlchemyTableBlockEntity.NOTEBOOK_SLOT));
                        be.setItem(AlchemyTableBlockEntity.NOTEBOOK_SLOT, ItemStack.EMPTY);
                    }

                    return InteractionResult.SUCCESS;
                }
            } else {
                // Player is NOT crouching but there is a notebook
                if (!level.isClientSide()) {
                    //TODO IsmMenus.open(new NotebookMenu(player, hand, notebookData));
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return getBlockEntity(level, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxY() && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return LOWER_SHAPE.get(state.getValue(FACING));
        } else {
            return UPPER_SHAPE.get(state.getValue(FACING));
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? belowState.isFaceSturdy(level, below, Direction.UP) : belowState.is(this);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER));
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
        DoubleBlockHalf half = state.getValue(HALF);
        if (directionToNeighbour.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (directionToNeighbour == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        } else {
            return neighbourState.getBlock() instanceof AlchemyTableBlock && neighbourState.getValue(HALF) != half
                    ? neighbourState.setValue(HALF, half)
                    : Blocks.AIR.defaultBlockState();
        }
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER
                ? new AlchemyTableBlockEntity(worldPosition, blockState)
                : null;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }
}
