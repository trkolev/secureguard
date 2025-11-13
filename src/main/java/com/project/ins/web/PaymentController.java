package com.project.ins.web;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.security.UserData;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class PaymentController {

    private final ClaimService claimService;

    public PaymentController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/payment-view")
    public ModelAndView upcomingPayment(@AuthenticationPrincipal UserData userData) {

        List<Claim> payments = claimService.upcomingPayments(userData.getId());

        ModelAndView modelAndView = new ModelAndView("payment-view");
        modelAndView.addObject("payments", payments);

        return modelAndView;
    }
}
