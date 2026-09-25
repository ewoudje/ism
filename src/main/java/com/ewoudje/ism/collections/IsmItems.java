package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.note.NoteItem;
import com.ewoudje.ism.features.notebook.NotebookData;
import com.ewoudje.ism.features.notebook.NotebookItem;
import com.ewoudje.ism.features.sadness.SadSeedItem;
import com.ewoudje.ism.util.item.IsmItem;
import com.ewoudje.ism.util.item.IsmItemProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class IsmItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(Ism.ID);
    private static final List<Supplier<? extends Item>> ISM_ITEMS = new ArrayList<>();

    public static final Supplier<SadSeedItem> SAD_SEED = registerItem("sad_seed", SadSeedItem::new);
    public static final Supplier<NotebookItem> NOTEBOOK = registerItem(
            "notebook",
            NotebookItem::new,
            p -> p
                    .loredCreator()
                    .stacksTo(1)
                    .component(IsmDataComponents.NOTEBOOK, new NotebookData())
    );
    public static final Supplier<NoteItem> NOTE = registerItem(
            "note",
            NoteItem::new,
            p -> p
                    .loredCreator()
                    .stacksTo(1)
    );

    public static final Supplier<IsmItem> SEALING_CHAIN = registerItem(
            "sealing_chains",
            IsmItem::new,
            p -> p
    );

    public static final Supplier<IsmItem> SEALING_ROCKS = registerItem(
            "sealing_rocks",
            IsmItem::new,
            p -> p
    );

    public static final Supplier<IsmItem> TRISTITIA_GROWTH_BUNDLE = registerItem(
            "tristitia_growth_bundle",
            IsmItem::new,
            p -> p
                    .component(IsmDataComponents.NOTE, IsmNotes.TRISTITIA_GROWTH)
    );

    public static final Supplier<IsmItem> TRISTITIA_VINE_GROWTH_BUNDLE = registerItem(
            "tristitia_vine_growth_bundle",
            IsmItem::new,
            p -> p
                    .component(IsmDataComponents.NOTE, IsmNotes.TRISTITIA_VINE_GROWTH)
    );


    static {
        registerBlockItem("sealstone", IsmBlocks.SEAL_STONE);
        registerBlockItem("tristitia_vine_growth", IsmBlocks.TRISTITIA_VINE_GROWTH);
        registerBlockItem("alchemy_table", IsmBlocks.ALCHEMY_TABLE);
    }

    private static <T extends Item> Supplier<T> registerItem(String name, Function<IsmItemProperties, T> factory) {
        return registerItem(name, factory, UnaryOperator.identity());
    }

    private static <T extends Item> Supplier<T> registerItem(String name, Function<IsmItemProperties, T> factory, UnaryOperator<IsmItemProperties> properties) {
        var result = REGISTRY.register(name, key -> factory.apply(properties.apply(new IsmItemProperties(key))));
        ISM_ITEMS.add(result);
        return result;
    }

    private static <T extends Block> Supplier<BlockItem> registerBlockItem(String name, Supplier<T> block) {
        var result = REGISTRY.registerItem(name, properties -> new BlockItem(block.get(), properties));
        ISM_ITEMS.add(result);
        return result;
    }

    public static void addToIsmTab(CreativeModeTab.Output output) {
        ISM_ITEMS.forEach(s -> output.accept(s.get()));
    }
}
