package com.ewoudje.ism.util.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ClientEventHandler1<T> {

    StreamCodec<? super RegistryFriendlyByteBuf, T> event1();

}
