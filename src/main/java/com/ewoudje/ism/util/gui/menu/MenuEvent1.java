package com.ewoudje.ism.util.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface MenuEvent1<T> extends CustomMenu {

    StreamCodec<? super RegistryFriendlyByteBuf, T> event1();

    void handleEvent1(T event);

    default void sendEvent1(T event) {
        if (player().level().isClientSide()) {
            ClientMenuUtil.sendEvent(this, 1, event1(), event);
        } else {
            ServerMenuUtil.sendEvent(this, 1, event1(), event);
        }

        handleEvent1(event);
    }
}
