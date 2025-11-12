package com.project.ins.exception;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(String message) {
        super(message);
    }

    public ClaimNotFoundException() {
    }
}
