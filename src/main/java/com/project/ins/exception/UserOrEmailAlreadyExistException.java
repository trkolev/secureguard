package com.project.ins.exception;

public class UserOrEmailAlreadyExistException extends RuntimeException {
    public UserOrEmailAlreadyExistException(String message) {
        super(message);
    }

    public UserOrEmailAlreadyExistException() {
    }
}
