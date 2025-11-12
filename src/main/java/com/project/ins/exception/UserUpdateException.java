package com.project.ins.exception;

public class UserUpdateException extends RuntimeException {
    public UserUpdateException(String message) {
        super(message);
    }

    public UserUpdateException() {
    }
}
