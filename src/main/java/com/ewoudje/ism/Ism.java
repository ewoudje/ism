package com.ewoudje.ism;

import com.ewoudje.ism.collections.IsmCollections;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Ism.ID)
public class Ism
{
    public static final String ID = "ism";

    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Ism(IEventBus modEventBus, ModContainer modContainer)
    {

        IsmCollections.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, IsmConfig.SPEC);
    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(ID, id);
    }
}
