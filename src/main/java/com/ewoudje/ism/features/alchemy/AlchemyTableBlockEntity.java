package com.ewoudje.ism.features.alchemy;

import com.ewoudje.ism.collections.IsmBlockEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class AlchemyTableBlockEntity extends BaseContainerBlockEntity implements ItemOwner {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component DEFAULT_NAME = Component.translatable("container.ism.alchemy_table");
    public static int NOTEBOOK_SLOT = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize(28, ItemStack.EMPTY);
    public AlchemyTableBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(IsmBlockEntities.ALCHEMY_TABLE.get(), worldPosition, blockState);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.threeRows(containerId, inventory, this);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items, true);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(
                    this.getBlockPos(),
                    this.getBlockState(),
                    this.getBlockState(),
                    Block.UPDATE_CLIENTS
            );
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            output.store("notebook", ItemStack.OPTIONAL_CODEC, items.get(NOTEBOOK_SLOT));
            return output.buildResult();
        }
    }

    @Override
    public void onDataPacket(Connection net, ValueInput input) {
        input.read("notebook", ItemStack.OPTIONAL_CODEC).ifPresent(
                notebook -> items.set(NOTEBOOK_SLOT, notebook)
        );
    }

    @Override
    public Level level() {
        return getLevel();
    }

    @Override
    public Vec3 position() {
        return Vec3.atCenterOf(getBlockPos());
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return getBlockState().getValue(AlchemyTableBlock.FACING).getOpposite().toYRot();
    }
}
