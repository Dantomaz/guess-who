package com.myapp.guess_who.exception.customException;

public class TooManyImagesException extends RuntimeException {

    public TooManyImagesException(String message) {
        super(message);
    }
}