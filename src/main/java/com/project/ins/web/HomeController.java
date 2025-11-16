package com.project.ins.web;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.notification.client.dto.Notification;
import com.project.ins.notification.service.NotificationService;
import com.project.ins.policy.model.Policy;
import com.project.ins.policy.service.PolicyService;
import com.project.ins.security.UserData;
import com.project.ins.transaction.model.Transaction;
import com.project.ins.transaction.service.TransactionService;
import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.web.dto.PasswordChangeRequest;
import com.project.ins.web.dto.UpdateUserDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;
    private final PolicyService policyService;
    private final TransactionService transactionService;
    private final ClaimService claimService;
    private final NotificationService notificationService;

    public HomeController(UserService userService, PolicyService policyService, TransactionService transactionService, ClaimService claimService, NotificationService notificationService) {
        this.userService = userService;
        this.policyService = policyService;
        this.transactionService = transactionService;
        this.claimService = claimService;
        this.notificationService = notificationService;
    }

    @GetMapping("/home")
    public ModelAndView home(@AuthenticationPrincipal UserData userData) {

        User user = userService.findById(userData.getId());
        List<Policy> userPolicy = policyService.getAllByOwnerIdLimited(userData.getId());
        int userPolicySize = policyService.getAllByUserId(userData.getId()).size();
        Wallet wallet = user.getWallet();
        List<Transaction> transactions = transactionService.findAllByUserIdLimit(userData.getId());
        List<Claim> claims = claimService.findAllByOwnerIdLimit(userData.getId());
        int userClaimsCount = claimService.findAllByOwnerId(userData.getId()).size();
        BigDecimal totalCoverage = policyService.findTotalCoverage(userData.getId());
        BigDecimal totalPremium = policyService.findTotalPremium(userData.getId());
        int claimsThisYear = claimService.findClaimsThisYear(userData.getId());
        List<Claim> pendingPayments = claimService.upcomingPaymentsLimit(userData.getId());
        List<Notification> notifications = notificationService.getNotificationsLimit(userData.getId());
        int notificationsCount = notificationService.getNotificationsLimit(userData.getId()).size();

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userPolicy", userPolicy);
        modelAndView.addObject("userPolicySize", userPolicySize);
        modelAndView.addObject("wallet", wallet);
        modelAndView.addObject("transactions", transactions);
        modelAndView.addObject("claims", claims);
        modelAndView.addObject("userClaimsCount", userClaimsCount);
        modelAndView.addObject("totalCoverage", totalCoverage);
        modelAndView.addObject("totalPremium", totalPremium);
        modelAndView.addObject("claimsThisYear", claimsThisYear);
        modelAndView.addObject("pendingPayments", pendingPayments);
        modelAndView.addObject("notifications", notifications);
        modelAndView.addObject("notificationsCount", notificationsCount);

        return modelAndView;
    }

    @GetMapping("/profile")
    public ModelAndView profile(@AuthenticationPrincipal UserData userData) {

        User user = userService.findById(userData.getId());

        ModelAndView modelAndView = new ModelAndView("profile");
        UpdateUserDto updateUserDto = userService.mapUserToUpdateDto(user);
        modelAndView.addObject("user", updateUserDto);
        modelAndView.addObject("passwordChange", new PasswordChangeRequest());

        return modelAndView;
    }

    @PatchMapping("/profile/change-password")
    public ModelAndView changePassword(@Valid @ModelAttribute("passwordChange") PasswordChangeRequest passwordChange,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserData userData,
                                       RedirectAttributes redirectAttributes) {

        User user = userService.findById(userData.getId());

        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("profile");
            mav.addObject("user", userService.mapUserToUpdateDto(user));
            mav.addObject("passwordChange", passwordChange);
            return mav;
        }

        userService.changePassword(user, passwordChange);

        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        return new ModelAndView("redirect:/profile");
    }

    @PatchMapping("/profile/update")
    public ModelAndView updateUserInformation(@Valid @ModelAttribute("user") UpdateUserDto updateUserDto
            ,BindingResult bindingResult
            ,@AuthenticationPrincipal UserData userData
            ,RedirectAttributes redirectAttributes) {

        User user = userService.findById(userData.getId());

        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("profile");
            mav.addObject("user", userService.mapUserToUpdateDto(user));
            mav.addObject("passwordChange", new  PasswordChangeRequest());
            return mav;
        }

        userService.updateUserInformation(updateUserDto, user);
        redirectAttributes.addFlashAttribute("successMessage", "User information updated successfully!");

        return new ModelAndView("redirect:/profile");
    }

    @GetMapping("/notifications")
    public ModelAndView testNotifications(@AuthenticationPrincipal UserData userData) {

        List<Notification> notifications = notificationService.getNotifications(userData.getId());
        ModelAndView modelAndView = new ModelAndView("notification-view");
        modelAndView.addObject("notifications", notifications);
        return modelAndView;
    }
}
