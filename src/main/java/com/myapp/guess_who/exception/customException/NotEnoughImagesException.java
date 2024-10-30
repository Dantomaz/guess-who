package com.myapp.guess_who.exception.customException;

public class NotEnoughImagesException extends RuntimeException {

    public NotEnoughImagesException(String message) {
        super(message);
    }
}