package com.project.ins.web;

import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ModelAndView getAdminPanel() {

        List<User> users = userService.findAll();

        ModelAndView modelAndView = new ModelAndView("admin");
        modelAndView.addObject("users", users);

        return modelAndView;
    }


    @PatchMapping("/admin/users/{id}/role")
    public String updateRole(@PathVariable("id") UUID id, @RequestParam("role") String role, RedirectAttributes redirectAttributes) {

        userService.updateRole(id, role);
        redirectAttributes.addFlashAttribute("successMessage", "User role has been updated successfully");

        return "redirect:/admin";
    }

    @PatchMapping("/admin/users/{id}/{status}")
    public String disable(@PathVariable("id") UUID id, @PathVariable("status") String status, RedirectAttributes redirectAttributes) {

        userService.updateStatus(id, status);

            redirectAttributes.addFlashAttribute("successMessage", "User staus has been updated successfully");

        return "redirect:/admin";
    }

}
