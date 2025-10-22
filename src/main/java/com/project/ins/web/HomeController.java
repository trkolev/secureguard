package com.project.ins.web;

import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import com.project.ins.web.dto.PasswordChangeRequest;
import com.project.ins.web.dto.RegisterRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home() {


        return "home";
    }

    @GetMapping("/profile")
    public ModelAndView profile(@AuthenticationPrincipal UserData userData) {

        User user = userService.findById(userData.getId());

        ModelAndView mav = new ModelAndView("profile");
        RegisterRequest registerRequest = userService.mapUserToRegisterRequest(user);
//        TODO:  Map User to registerRequest
        mav.addObject("user", registerRequest);
        mav.addObject("passwordChange", new PasswordChangeRequest());

        return mav;
    }

}
