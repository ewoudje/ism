package com.ewoudje.ism.util.item;

import com.ewoudje.ism.collections.IsmDataComponents;
import com.ewoudje.ism.util.lore.Creator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class IsmItem extends Item {
    private final boolean shouldShowCreator;

    public IsmItem(IsmItemProperties properties) {
        super(properties);

        this.shouldShowCreator = properties.shouldShowCreator;
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, Player player) {
        itemStack.set(IsmDataComponents.CREATOR, Creator.ofPlayer(player));
        super.onCraftedBy(itemStack, player);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        Creator creator = itemStack.get(IsmDataComponents.CREATOR);
        if (creator != null
                && display.shows(IsmDataComponents.CREATOR.get())
                && (tooltipFlag.isAdvanced() || shouldShowCreator)
        ) {
            builder.accept(makeCreatorTooltip(context.player(), itemStack, creator));
        }

        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }

    protected Component makeCreatorTooltip(Player player, ItemStack itemStack, Creator creator) {
        return Component.translatable("item.ism.creator", creator.asComponent(player));
    }
}
