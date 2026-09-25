package com.ewoudje.ism.features.note;

import com.ewoudje.ism.collections.IsmDataComponents;
import com.ewoudje.ism.util.item.IsmItem;
import com.ewoudje.ism.util.item.IsmItemProperties;
import com.ewoudje.ism.util.lore.Creator;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NoteItem extends IsmItem {

    public NoteItem(IsmItemProperties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return InteractionResult.PASS;
        var noteData = stack.get(IsmDataComponents.NOTE);
        if (noteData == null) return InteractionResult.PASS;

        player.awardStat(Stats.ITEM_USED.get(this));

        if (level.isClientSide() && player instanceof LocalPlayer localPlayer) {
            NoteScreen.open(localPlayer, hand, noteData);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected Component makeCreatorTooltip(Player player, ItemStack itemStack, Creator creator) {
        return Component.translatable("item.ism.author", creator.asComponent(player));
    }
}
