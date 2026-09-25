package com.ewoudje.ism.features.notebook;

import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface NotebookPlaceableCapability {

    void placeNotebook(ItemStack notebook);

    @Nullable NotebookData notebookData();

}
