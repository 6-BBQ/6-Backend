package com.sixbbq.gamept.exception;

public class ServerDownException extends RuntimeException {
    public ServerDownException(String message) {
        super(message);
    }
}
