package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.tristitia.growth.TristitiaGrowthWorldState;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class IsmAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTERY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ism.ID);

    public static final Supplier<AttachmentType<TristitiaGrowthWorldState>> TRISTITIA_GROWTH_STATE = REGISTERY.register("tristitia_growth_state",
            () -> AttachmentType.builder(TristitiaGrowthWorldState::new).build());


    private static <T> IAttachmentSerializer<T> codecSerializer(Function<IAttachmentHolder, MapCodec<T>> mapCodec) {
        return new IAttachmentSerializer<>() {
            @Override
            public T read(IAttachmentHolder holder, ValueInput input) {
                final Optional<T> parsingResult = input.read(mapCodec.apply(holder));
                return parsingResult.orElseThrow(() -> buildException("read"));
            }

            @Override
            public boolean write(T attachment, ValueOutput output) {
                output.store(mapCodec.apply(null), attachment);
                return true;
            }

            private RuntimeException buildException(final String operation) {
                return new IllegalStateException("Unable to " + operation + " attachment due to an internal codec error.");
            }
        };
    }
}
