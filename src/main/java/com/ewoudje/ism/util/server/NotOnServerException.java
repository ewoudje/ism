package com.ewoudje.ism.util.world;

public class NotOnServerException extends RuntimeException {
    public NotOnServerException() {
        super("Should be called logical serverside");
    }
}
