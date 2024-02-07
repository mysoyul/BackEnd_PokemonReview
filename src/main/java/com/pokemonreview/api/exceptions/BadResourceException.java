package com.pokemonreview.api.exceptions;

public class BadResourceException extends MyResourceException {

    public BadResourceException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 400;
    }
}