package com.aaa.vibesmusic.exceptions;

public class SongAlreadyExistsException extends IllegalArgumentException {
    public SongAlreadyExistsException() {
        super("This song already exists!");
    }
}
