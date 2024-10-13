package com.myapp.guess_who.exception.customException;

public class NoSuchRoomException extends RuntimeException {

    public NoSuchRoomException(String message) {
        super(message);
    }
}
