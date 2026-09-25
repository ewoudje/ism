package com.ewoudje.ism.features.note;

import com.ewoudje.ism.features.notebook.NotebookData;
import com.ewoudje.ism.util.lore.Creator;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.slf4j.Logger;

import java.util.function.IntSupplier;

public class DraggableNote implements GuiEventListener, Renderable, NarratableEntry {
    private static Logger LOGGER = LogUtils.getLogger();
    private final Creator creator;
    private final Note note;
    private final float rot;
    private final IntSupplier upOrder;
    private final int noteWidth, noteHeight;
    private ScreenRectangle notesBounds;
    private int zOrder;
    private float x, y;
    private ScreenRectangle bounds;
    private boolean isFocused, hasFailed;


    public DraggableNote(NotebookData.NoteEntry entry, ScreenRectangle notesBounds, IntSupplier upOrder) {
        this.note = entry.note();
        this.creator = entry.creator();
        this.rot = entry.rot();
        this.x = entry.x() + notesBounds.left();
        this.y = entry.y() + notesBounds.top();
        this.zOrder = entry.height();
        this.notesBounds = notesBounds;
        this.upOrder = upOrder;

        NoteRenderer renderer = note.renderer();
        this.noteWidth = renderer.getWidth(note);
        this.noteHeight = renderer.getHeight(note);

        updateBounds();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (hasFailed) return;

        // Handy outline
        // graphics.outline(bounds.left(), bounds.top(), bounds.width(), bounds.height(), -1);

        var pose = graphics.pose();
        pose.pushMatrix();
        try {
            doTransform(pose);

            NoteRenderer renderer = note.renderer();
            renderer.extractBG(note, graphics, mouseX, mouseY, a);
            renderer.extract(note, graphics, mouseX, mouseY, a);

        } catch (Exception e) {
            hasFailed = true;
            LOGGER.error("Failed to render draggable note", e);
        } finally {
            pose.popMatrix();
        }
    }

    private Matrix3x2f doTransform(Matrix3x2f m) {
        m.translate(x, y);
        float hW = noteWidth / 2f;
        float hH = noteHeight / 2f;
        m.translate(hW, hH);
        m.rotate(rot);
        m.translate(-hW, -hH);
        m.scale(0.8f);
        return m;
    }

    @Override
    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        zOrder = upOrder.getAsInt();

        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        x = x + (float) dx;Math.clamp(x + (float) dx, notesBounds.left(), notesBounds.right());
        y = y + (float) dy;
        if (x < notesBounds.left()) x = notesBounds.left();
        if (y < notesBounds.top()) y = notesBounds.top();
        if (x > notesBounds.right() - bounds.width()) x = notesBounds.right() - bounds.width();
        if (y > notesBounds.bottom() - bounds.height()) y = notesBounds.bottom() - bounds.height();
        zOrder = upOrder.getAsInt();

        updateBounds();

        return true;
    }

    private void updateBounds() {
        var flat = new ScreenRectangle(0, 0, noteWidth, noteHeight);
        this.bounds = flat.transformMaxBounds(doTransform(new Matrix3x2f()));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (bounds.containsPoint((int) mouseX, (int) mouseY)) {
            Matrix3x2f matrix = doTransform(new Matrix3x2f()).invert();
            Vector2f vec = matrix.transformPosition((float) mouseX, (float) mouseY, new Vector2f());
            if (vec.x < 0 || vec.y < 0 || vec.x > noteWidth || vec.y > noteHeight) return false;

            return !((NoteRenderer) note.renderer()).isTranslucentSample(note, vec);
        }

        return false;
    }

    public NotebookData.NoteEntry getUpdated() {
        return new NotebookData.NoteEntry(creator, note, zOrder, x - notesBounds.left(), y - notesBounds.top(), rot);
    }

    public int zOrder() {
        return zOrder;
    }


    @Override
    public ScreenRectangle getRectangle() {
        return bounds;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {

    }

    public void updateNoteBounds(ScreenRectangle notesBound) {
        float x = this.x - this.notesBounds.left();
        float y = this.y - this.notesBounds.top();
        this.x = notesBound.left() + x;
        this.y = notesBound.top() + y;

        this.notesBounds = notesBound;
        updateBounds();
    }
}
