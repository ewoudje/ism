package com.ewoudje.ism.features.notebook;

import com.ewoudje.ism.collections.IsmDataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface NotebookContainingCapability {

    void placeNotebook(ItemStack notebook);
    ItemStack takeNotebook();
    @Nullable NotebookData notebookData();

    class ContainerCapability implements NotebookContainingCapability {
        private final Container container;
        private final int slot;

        public ContainerCapability(Container container, int slot) {
            this.container = container;
            this.slot = slot;
        }

        @Override
        public void placeNotebook(ItemStack notebook) {
            container.setItem(slot, notebook);
        }

        @Override
        public ItemStack takeNotebook() {
            return container.removeItem(slot, 1);
        }

        @Override
        public @Nullable NotebookData notebookData() {
            return container.getItem(slot).get(IsmDataComponents.NOTEBOOK);
        }
    }
}
