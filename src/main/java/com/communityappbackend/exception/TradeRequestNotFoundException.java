package com.communityappbackend.exception;

public class TradeRequestNotFoundException extends RuntimeException {
    public TradeRequestNotFoundException(String message) {
        super(message);
    }
}
