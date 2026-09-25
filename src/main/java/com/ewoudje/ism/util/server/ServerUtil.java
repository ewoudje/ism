package com.ewoudje.ism.util.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class ServerUtil {

    public static ServerLevel getLevel(Level level) {
        if (level instanceof ServerLevel sl) {
            return sl;
        } else {
            if (level.isClientSide()) throw new NotOnServerException();
            throw new IllegalStateException("Got an level that isn't a server level?");
        }
    }

}
