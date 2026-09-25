package com.ewoudje.ism.features.notebook;

import com.ewoudje.ism.collections.IsmDataComponents;
import com.ewoudje.ism.collections.IsmMenus;
import com.ewoudje.ism.features.note.Note;
import com.ewoudje.ism.util.item.IsmItem;
import com.ewoudje.ism.util.item.IsmItemProperties;
import com.ewoudje.ism.util.lore.Creator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NotebookItem extends IsmItem {
    public NotebookItem(IsmItemProperties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        var stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return InteractionResult.PASS;
        var component = stack.get(IsmDataComponents.NOTEBOOK);
        if (component == null) return InteractionResult.PASS;

        if (!level.isClientSide()) {
            IsmMenus.open(new NotebookMenu(player, hand, component));
            return InteractionResult.SUCCESS_SERVER;
        } else return InteractionResult.SUCCESS;
    }

    public static void addNoteToNotebook(AddNoteToNotebookPacket p, IPayloadContext context) {
        addNoteToNotebook(context.player().getInventory(), p.noteSlot(), p.notebookSlot());
    }

    public static void addNoteToNotebook(Inventory inventory, int noteSlot, int notebookSlot) {
        var note = inventory.getItem(noteSlot);
        var notebook = inventory.getItem(notebookSlot);
        if (note.isEmpty() || notebook.isEmpty()) return;
        Creator creator = note.get(IsmDataComponents.CREATOR);
        Note noteData = note.get(IsmDataComponents.NOTE);
        NotebookData notebookData = notebook.get(IsmDataComponents.NOTEBOOK);
        if (notebookData == null || noteData == null) return;

        notebook.set(IsmDataComponents.NOTEBOOK, notebookData.addNote(creator, noteData));
        note.shrink(1);
    }

    @Override
    protected Component makeCreatorTooltip(Player player, ItemStack itemStack, Creator creator) {
        return Component.translatable("item.ism.author", creator.asComponent(player));
    }
}
