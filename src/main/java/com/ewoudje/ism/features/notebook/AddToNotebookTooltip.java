package com.ewoudje.ism.features.notebook;

import com.ewoudje.ism.collections.IsmDataComponents;
import com.ewoudje.ism.collections.IsmKeymaps;
import com.ewoudje.ism.features.note.Note;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber
public class AddToNotebookTooltip {
    private static final int HOLD_TIME = 30;
    private static int progress = -1;
    private static @Nullable Slot slot;
    private static @Nullable Player player;
    private static ItemStack item = ItemStack.EMPTY;
    private static int notebookSlot = -1;

    @SubscribeEvent
    private static void onClientTick(ClientTickEvent.Post event) {
        fetchData();

        if (canProgress()) {
            progress++;
        } else if (progress >= 0) {
            progress--;
        }

        if (progress > HOLD_TIME) {
            addToNotebook();
        }
    }

    private static void fetchData() {
        player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!(Minecraft.getInstance().gui.screen() instanceof AbstractContainerScreen<?> screen)) return;

        // Reset progress if diff slot
        if (screen.getHoveredSlot() != slot) {
            progress = -1;
        }

        slot = screen.getHoveredSlot();
        if (slot == null) return;

        item = slot.getItem();

        notebookSlot = -1;
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).has(IsmDataComponents.NOTEBOOK)) {
                notebookSlot = i;
                break;
            }
        }
    }

    private static boolean canProgress() {
        if (player == null || item.isEmpty() || slot == null) return false;
        if (!slot.allowModification(player)) return false;

        Note note = item.get(IsmDataComponents.NOTE);
        if (note == null) return false;

        int keyCode = IsmKeymaps.ADD_TO_NOTEBOOK.get().getKey().getValue();
        return InputConstants.isKeyDown(keyCode) &&
                IsmKeymaps.ADD_TO_NOTEBOOK.get().isConflictContextAndModifierActive();
    }

    private static void addToNotebook() {
        if (player == null || slot == null || notebookSlot == -1) return;

        progress = -1; // Reset progress when consuming an item
        var inv = player.getInventory();
        NotebookItem.addNoteToNotebook(inv, slot.getSlotIndex(), notebookSlot);
        ClientPacketDistributor.sendToServer(new AddNoteToNotebookPacket(slot.getSlotIndex(), notebookSlot));
    }

    @SubscribeEvent
    private static void onItemTooltip(RenderTooltipEvent.GatherComponents event) {
        if (item.isEmpty() || player == null || slot == null || notebookSlot == -1) return;
        if (!item.has(IsmDataComponents.NOTE) || !slot.allowModification(player)) return;

        if (progress == -1) {
            event.getTooltipElements().add(Either.left(
                    Component.translatable(
                                    "item.ism.hold_to_add_to_notebook",
                                    Component.keybind(IsmKeymaps.ADD_TO_NOTEBOOK.get().getName())
                            )
                            .withStyle(ChatFormatting.DARK_PURPLE)
            ));
        } else  {
            var component = Component.empty();
            int zTo10 = (progress * 10) / HOLD_TIME;

            component.append("[");
            for (int i = 0; i < 10; i++) {
                if (i <= zTo10) {
                    component.append("|");
                } else  {
                    component.append(".");
                }
            }
            component.append("]");

            event.getTooltipElements().add(Either.left(component.withStyle(ChatFormatting.DARK_PURPLE)));
        }
    }
}
