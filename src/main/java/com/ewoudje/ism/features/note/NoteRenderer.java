package com.ewoudje.ism.features.note;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.collections.IsmAtlases;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.joml.Vector2f;

public interface NoteRenderer<T extends Note> {

    int getWidth(T data);
    int getHeight(T data);

    void extract(T data, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
    void extractBG(T data, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);

    default boolean isTranslucentSample(T data, Vector2f vec) {
        return false;
    }

    enum SimpleTextureRenderer implements NoteRenderer<Note.SimpleTexture> {
        WITH_BACKGROUND,
        WITHOUT_BACKGROUND;
        private static final Identifier BACKGROUND = Ism.id("note_background");

        @Override
        public int getWidth(Note.SimpleTexture data) {
            var sprite = this == WITH_BACKGROUND ? getSprite(BACKGROUND) : getSprite(data.texture());
            return sprite.contents().width();
        }

        @Override
        public int getHeight(Note.SimpleTexture data) {
            var sprite = this == WITH_BACKGROUND ? getSprite(BACKGROUND) : getSprite(data.texture());
            return sprite.contents().height();
        }

        @Override
        public void extract(Note.SimpleTexture data, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            var sprite = getSprite(data.texture());
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    sprite,
                    0, 0,
                    sprite.contents().width(),
                    sprite.contents().height()
            );
        }

        @Override
        public void extractBG(Note.SimpleTexture data, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            if (this == WITHOUT_BACKGROUND) return;

            var sprite = getSprite(BACKGROUND);
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    sprite,
                    0, 0,
                    sprite.contents().width(),
                    sprite.contents().height()
            );
        }

        @Override
        public boolean isTranslucentSample(Note.SimpleTexture data, Vector2f vec) {
            var sprite = this == WITH_BACKGROUND ? getSprite(BACKGROUND) : getSprite(data.texture());
            return ARGB.alpha(sprite.getPixelRGBA(0, (int) vec.x, (int) vec.y)) == 0;
        }

        private static TextureAtlasSprite getSprite(Identifier texture) {
            return IsmAtlases.NOTES.getSprite(texture);
        }
    }
}
