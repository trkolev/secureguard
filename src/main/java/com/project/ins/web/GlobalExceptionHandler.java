package com.project.ins.web;

import com.project.ins.exception.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserUpdateException.class)
    public RedirectView handleUserUpdateException(UserUpdateException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "User update failed!");
        return new RedirectView("/admin");
    }

    @ExceptionHandler(ClaimNotFoundException.class)
    public RedirectView handleClaimNotFoundException(ClaimNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Claim not found!");
        return new RedirectView("/employee");
    }

    @ExceptionHandler(WrongPasswordException.class)
    public RedirectView handleWrongPasswordException(WrongPasswordException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Current password is wrong!");
        return new RedirectView("/profile");
    }

    @ExceptionHandler(PasswordDifferException.class)
    public RedirectView handleWrongPasswordException(PasswordDifferException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "New password and confirmation differs!");
        return new RedirectView("/profile");
    }

    @ExceptionHandler(UserOrEmailAlreadyExistException.class)
    public RedirectView handleUserOrEmailAlreadyExistException(UserOrEmailAlreadyExistException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "User or email already exist!");
        return new RedirectView("/register");
    }

    @ExceptionHandler(RegisterPasswordDifferException.class)
    public RedirectView handleRegisterPasswordDifferException(RegisterPasswordDifferException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match!");
        redirectAttributes.addFlashAttribute("registerRequest", ex.getRegisterRequest());
        return new RedirectView("/register");
    }

    @ExceptionHandler(Exception.class)
    public String globalExceptionHandler(Exception ex) {
        return "error";
    }

}
