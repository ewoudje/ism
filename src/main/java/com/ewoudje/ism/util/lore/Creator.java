package com.ewoudje.ism.util.lore;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public sealed interface Creator {
    Codec<Creator> CODEC = Codec.either(Player.CODEC, Meta.CODEC).xmap(Either::unwrap, c -> switch (c) {
        case Player p ->  Either.left(p);
        case Meta m -> Either.right(m);
    });

    StreamCodec<ByteBuf, Creator> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    static Creator ofPlayer(net.minecraft.world.entity.player.Player player) {
        return new Player(player.getUUID(), player.getPlainTextName());
    }

    Component asComponent(net.minecraft.world.entity.player.Player player);

    //Im aware there is nameAndId
    record Player(UUID id, String name) implements Creator {
        public static final Codec<Player> CODEC = RecordCodecBuilder.create(i -> i.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(Player::id),
                Codec.STRING.fieldOf("name").forGetter(Player::name)
        ).apply(i, Player::new));

        public static final StreamCodec<ByteBuf, Player> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, Player::id,
                ByteBufCodecs.STRING_UTF8, Player::name,
                Player::new
        );

        @Override
        public Component asComponent(net.minecraft.world.entity.player.Player player) {
            return Component.literal(name);
        }
    }

    record Meta(String name) implements Creator {
        public static final Codec<Meta> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("meta").forGetter(Meta::name)
        ).apply(i, Meta::new));


        //TODO should be controlled if the player knows the meta creator or not
        @Override
        public Component asComponent(net.minecraft.world.entity.player.Player player) {
            if (name.equals("unknown"))
                return Component.translatable("creator.ism.unknown");

            return Component.literal(name)
                    .withStyle(ChatFormatting.OBFUSCATED);
        }
    }
}
