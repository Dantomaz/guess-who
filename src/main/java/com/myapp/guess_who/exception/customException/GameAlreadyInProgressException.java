package com.myapp.guess_who.exception.customException;

public class GameAlreadyInProgressException extends RuntimeException {

    public GameAlreadyInProgressException(String message) {
        super(message);
    }
}
