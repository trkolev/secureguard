package com.project.ins.web;

import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class WalletController {

    private final UserService userService;
    private final WalletService walletService;

    public WalletController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @PatchMapping("/wallets/{id}/balance")
    public String addBalance(@PathVariable UUID id, @AuthenticationPrincipal UserData userData, RedirectAttributes redirectAttributes) {

        User user = userService.findById(userData.getId());
        walletService.topUp(user);

        redirectAttributes.addFlashAttribute("successMessage", "You successfully added 200 EUR to your balance!");

        return "redirect:/home";
    }

}
