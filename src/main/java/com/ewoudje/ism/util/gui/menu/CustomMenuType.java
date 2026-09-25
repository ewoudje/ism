package com.ewoudje.ism.util.gui.menu;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.collections.IsmRegistries;
import com.ewoudje.ism.util.gui.screen.CustomScreen;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CustomMenuType<T extends CustomMenu> {
    ResourceKey<Registry<CustomMenuType<?>>> KEY = ResourceKey.createRegistryKey(Ism.id("menu_type"));

    T initMenu(Player player, ValueInput input);
    CustomScreen<T> initScreen(T menu);

    static <T extends CustomMenu> Supplier<CustomMenuType<T>> create(BiFunction<Player, ValueInput, T> menuFactory, Function<T, CustomScreen<T>> screenFactory) {
        return new SimpleType<>(menuFactory, screenFactory);
    }

    record SimpleType<T extends CustomMenu> (
            BiFunction<Player, ValueInput, T> menuFactory,
            Function<T, CustomScreen<T>> screenFactory
    ) implements CustomMenuType<T>, Supplier<CustomMenuType<T>> {

        @Override
        public T initMenu(Player player, ValueInput input) {
            return menuFactory.apply(player, input);
        }

        @Override
        public CustomScreen<T> initScreen(T menu) {
            return screenFactory.apply(menu);
        }

        @Override
        public CustomMenuType<T> get() {
            return this;
        }

        @Override
        public String toString() {
            Identifier id = IsmRegistries.CUSTOM_MENU_TYPES.getKey(this);
            if (id != null)
                return id.toString();
            else
                return "unknown";
        }
    }
}
