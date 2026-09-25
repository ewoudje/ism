package com.ewoudje.ism.features.note;

import com.ewoudje.ism.collections.IsmDataComponents;
import com.ewoudje.ism.features.notebook.AddNoteToNotebookPacket;
import com.ewoudje.ism.features.notebook.NotebookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class NoteScreen extends Screen {

    private static final int TEXTURE_WIDTH = 130;
    private static final int TEXTURE_HEIGHT = 158;
    private static final int BUTTON_WIDTH = 130;
    private static final int BUTTON_HEIGHT = 20;

    private final LocalPlayer player;
    private final int note;
    private final int notebook;
    private final Note noteData;

    protected NoteScreen(LocalPlayer player, InteractionHand hand, Note noteData) {

        super(Component.empty());
        this.noteData = noteData;
        this.player = player;
        this.note = hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : 40;

        int notebook = -1;
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).has(IsmDataComponents.NOTEBOOK)) {
                notebook = i;
                break;
            }
        }

        this.notebook = notebook;
    }

    @Override
    protected void init() {
        super.init();
        if (notebook != -1) {
            addRenderableWidget(Button.builder(Component.translatable("gui.ism.add_to_notebook"), this::addToNotebook)
                    .pos((width - BUTTON_WIDTH) / 2, top() + TEXTURE_HEIGHT + 10)
                    .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build());
        }
    }

    private void addToNotebook(Button button) {
        NotebookItem.addNoteToNotebook(player.getInventory(), note, notebook);
        ClientPacketDistributor.sendToServer(new AddNoteToNotebookPacket(note, notebook));
        Minecraft.getInstance().gui.setScreen(null);
    }

    private int left() {
        return (width - TEXTURE_WIDTH) / 2;
    }

    private int top() {
        return (height - TEXTURE_HEIGHT - 32) / 2;
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        var pose = graphics.pose();

        pose.pushMatrix();
        pose.translate(left(), top());
        ((NoteRenderer) noteData.renderer()).extractBG(noteData, graphics, mouseX, mouseY, partialTick);
        pose.popMatrix();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        var pose = graphics.pose();

        pose.pushMatrix();
        pose.translate(left(), top());
        ((NoteRenderer) noteData.renderer()).extract(noteData, graphics, mouseX, mouseY, partialTick);
        pose.popMatrix();

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    public static void open(LocalPlayer player, InteractionHand hand, Note noteData) {
        Minecraft.getInstance().gui.setScreen(new NoteScreen(player, hand, noteData));
    }
}
