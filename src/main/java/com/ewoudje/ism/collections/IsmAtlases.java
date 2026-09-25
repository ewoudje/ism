package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class IsmAtlases {
    private static final List<AtlasDefinition> ATLASES = new ArrayList<>();

    public static final AtlasDefinition NOTES = register("notes");

    private static AtlasDefinition register(String name) {
        var result = new AtlasDefinition(
                Ism.id("textures/atlas/" + name + ".png"),
                Ism.id(name)
        );
        ATLASES.add(result);
        return result;
    }

    @SubscribeEvent
    private static void registerAtlases(RegisterTextureAtlasesEvent event) {
        for (AtlasDefinition definition : ATLASES) {
            event.register(new AtlasManager.AtlasConfig(
                    definition.sheet,
                    definition.atlasId,
                    false
            ));
        }
    }

    public static class AtlasDefinition {
        private final Identifier sheet, atlasId;
        private TextureAtlas atlas = null;

        public AtlasDefinition(Identifier sheet, Identifier atlasId) {
            this.sheet = sheet;
            this.atlasId = atlasId;
        }

        public TextureAtlas atlas() {
            if (atlas == null) {
                atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(atlasId);
            }

            return atlas;
        }

        public TextureAtlasSprite getSprite(Identifier texture) {
            return atlas().getSprite(texture);
        }

        public Identifier sheet() {
            return sheet;
        }

        public Identifier atlasId() {
            return atlasId;
        }
    }
}
