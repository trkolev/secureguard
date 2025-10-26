package com.project.ins.web;

import com.project.ins.policy.model.Policy;
import com.project.ins.policy.service.PolicyService;
import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import com.project.ins.web.dto.PasswordChangeRequest;
import com.project.ins.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.boot.Banner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PolicyService policyService;

    public HomeController(UserService userService, PasswordEncoder passwordEncoder, PolicyService policyService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.policyService = policyService;
    }

    @GetMapping("/home")
    public ModelAndView home(@AuthenticationPrincipal UserData userData) {

        User user = userService.findById(userData.getId());
        List<Policy> userPolicy = policyService.getAllByOwnerIdLimited(userData.getId());
        int userPolicySize = policyService.getAllByUserId(userData.getId()).size();

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userPolicy", userPolicy);
        modelAndView.addObject("userPolicySize", userPolicySize);

        return modelAndView;
    }

    @GetMapping("/profile")
    public ModelAndView profile(@AuthenticationPrincipal UserData userData) {

        User user = userService.findById(userData.getId());

        ModelAndView modelAndView = new ModelAndView("profile");
        RegisterRequest registerRequest = userService.mapUserToRegisterRequest(user);
//        TODO:  Map User to registerRequest
        modelAndView.addObject("user", registerRequest);
        modelAndView.addObject("passwordChange", new PasswordChangeRequest());

        return modelAndView;
    }

    @PostMapping("/profile/change-password")
    public ModelAndView changePassword(@Valid @ModelAttribute("passwordChange") PasswordChangeRequest passwordChange, 
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserData userData,
                                       RedirectAttributes redirectAttributes) {

        // Get user data for returning to profile on error
        User user = userService.findById(userData.getId());

        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("profile");
            mav.addObject("user", userService.mapUserToRegisterRequest(user));
            mav.addObject("passwordChange", passwordChange);
            return mav;
        }

        if(!passwordEncoder.matches(passwordChange.getCurrentPassword(), userData.getPassword())) {
            bindingResult.rejectValue("currentPassword", "error.currentPassword", "Current password is incorrect");
            ModelAndView mav = new ModelAndView("profile");
            mav.addObject("user", userService.mapUserToRegisterRequest(user));
            mav.addObject("passwordChange", passwordChange);
            return mav;
        }

        if (!passwordChange.getNewPassword().equals(passwordChange.getConfirmPassword())) {
            bindingResult.rejectValue("newPassword", "error.newPassword", "Passwords do not match");
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            ModelAndView mav = new ModelAndView("profile");
            mav.addObject("user", userService.mapUserToRegisterRequest(user));
            mav.addObject("passwordChange", passwordChange);
            return mav;
        }

        user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
        userService.save(user);
        
        // Add success message as flash attribute
        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        
        // Redirect to profile with success message
        return new ModelAndView("redirect:/profile");
    }

}
