package com.project.ins.exception;

public class PasswordDifferException extends RuntimeException {
    public PasswordDifferException(String message) {
        super(message);
    }

    public PasswordDifferException() {
    }
}
