package com.ewoudje.ism.util.network;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.codec.StreamCodec;

public class IsmByteBufCodes {
    public static <B, T1, T2> StreamCodec<B, Pair<T1, T2>> pair(StreamCodec<? super B, T1> a, StreamCodec<? super B, T2> b) {
        return StreamCodec.composite(
                a, Pair::getFirst,
                b, Pair::getSecond,
                Pair::new
        );
    }
}
