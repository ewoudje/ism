package com.ewoudje.ism.features.notebook;

import com.ewoudje.ism.collections.IsmDataComponents;
import com.ewoudje.ism.collections.IsmMenus;
import com.ewoudje.ism.util.gui.menu.AbstractCustomMenu;
import com.ewoudje.ism.util.gui.menu.CustomMenuType;
import com.ewoudje.ism.util.gui.menu.MenuEvent1;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class NotebookMenu extends AbstractCustomMenu implements MenuEvent1<NotebookData> {
    private final InteractionHand hand;
    private NotebookData data;

    public NotebookMenu(Player player, ValueInput input) {
        this(
                player,
                input.getBooleanOr("mainHand", true) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
                input.read("data", NotebookData.CODEC).orElseThrow()
        );
    }

    public NotebookMenu(Player player, InteractionHand hand, NotebookData data) {
        super(player);
        this.data = data;
        this.hand = hand;
    }

    public NotebookData data() {
        return data;
    }

    @Override
    public void toClientData(ValueOutput output) {
        output.store("data", NotebookData.CODEC, data);
        output.putBoolean("mainHand", hand == InteractionHand.MAIN_HAND);
    }

    @Override
    public CustomMenuType<?> type() {
        return IsmMenus.NOTEBOOK.get();
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, NotebookData> event1() {
        return NotebookData.STREAM_CODEC;
    }

    @Override
    public void handleEvent1(NotebookData data) {
        this.data = data;
        player.getItemInHand(hand).set(IsmDataComponents.NOTEBOOK, data);
    }
}
