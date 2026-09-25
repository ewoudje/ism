package com.ewoudje.ism.util.server;

public class NotOnServerException extends RuntimeException {
    public NotOnServerException() {
        super("Should be called logical serverside");
    }
}
