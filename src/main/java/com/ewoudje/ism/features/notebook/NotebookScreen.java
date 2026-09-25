package com.ewoudje.ism.features.notebook;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.note.DraggableNote;
import com.ewoudje.ism.util.gui.screen.AbstractCustomScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NotebookScreen extends AbstractCustomScreen<NotebookMenu> {
    private static final Identifier TEXTURE = Ism.id("textures/gui/notebook.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int BACKGROUND_WIDTH = 256;
    private static final int BACKGROUND_HEIGHT = 176;

    private static final int ARROWS_TOP = 176;
    private static final int ARROWS_LEFT = 0;
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_HEIGHT = 7;

    private @Nullable List<DraggableNote> notes;
    private @Nullable ScreenRectangle notesBound;
    private int zOrderCounter;
    private int tickCounter;

    public NotebookScreen(NotebookMenu menu, Component title) {
        super(menu, title);
    }

    @Override
    protected void init() {
        super.init();

        notesBound = new ScreenRectangle(left() + 12, top() + 8, BACKGROUND_WIDTH - 12, BACKGROUND_HEIGHT - 8);

        if (notes == null) {
            notes = menu.data().collectedNotes().stream()
                    .map(n -> new DraggableNote(n, notesBound, this::newZOrder))
                    .toList();
        } else {
            notes.forEach(note -> note.updateNoteBounds(notesBound));
        }

        zOrderCounter = notes.stream().mapToInt(DraggableNote::zOrder).max().orElse(0);
        notes.forEach(this::addWidget);
    }

    @Override
    public void tick() {
        super.tick();

        // If game crashes, or internet dies, dont lose all notebook progress
        if (tickCounter++ % 100 == 0) {
            sendToServer();
        }
    }

    private int newZOrder() {
        return zOrderCounter++;
    }

    private int left() {
        return (width - BACKGROUND_WIDTH) / 2;
    }

    private int top() {
        return (height - BACKGROUND_HEIGHT + 10) / 2;
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                left(), top(),
                0, 0,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        notes.stream()
                .sorted(Comparator.comparingInt(DraggableNote::zOrder))
                .forEachOrdered(n -> n.extractRenderState(graphics, mouseX, mouseY, a));

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double x, double y) {
        return (Optional) notes.stream()
                .sorted(Comparator.comparingInt(n -> -n.zOrder()))
                .filter(n -> n.isMouseOver(x, y))
                .findFirst();
    }

    @Override
    public void onClose() {
        sendToServer();
        super.onClose();
    }

    private void sendToServer() {
        menu.sendEvent1(new NotebookData(notes.stream().map(DraggableNote::getUpdated).toList()));
    }
}
