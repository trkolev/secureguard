package com.project.ins.web;

import com.project.ins.policy.model.Policy;
import com.project.ins.policy.service.PolicyService;
import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.service.WalletService;
import com.project.ins.web.dto.PolicyRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class PolicyController {

    private final PolicyService policyService;
    private final UserService userService;
    private final WalletService walletService;

    public PolicyController(PolicyService policyService, UserService userService, UserService userService1, WalletService walletService) {
        this.policyService = policyService;
        this.userService = userService1;
        this.walletService = walletService;
    }

    @GetMapping("/policy")
    public ModelAndView showPolicyForm() {
        ModelAndView mav = new ModelAndView("policy");
        mav.addObject("policyRequest", new PolicyRequest());
        return mav;
    }

    @PostMapping("/policy/create")
    public ModelAndView createPolicy(@Valid PolicyRequest policyRequest,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserData userData,
                                     RedirectAttributes redirectAttributes
                                    ) {
        // Handle policy creation

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("policyRequest", policyRequest);
            return new ModelAndView("redirect:/policy/create");
        }

        User user = userService.findById(userData.getId());
        policyService.createPolicy(policyRequest, userData, user);
        walletService.reduceAmount(policyRequest.getPremiumAmount(), user);
        redirectAttributes.addFlashAttribute("successMessage", "Policy created successfully");

        return new ModelAndView("redirect:/policy");
    }

    @GetMapping("/policy-view")
    public ModelAndView listPolicies(@AuthenticationPrincipal UserData userData) {

        List<Policy> userPolicy = policyService.getAllByUserId(userData.getId());
        ModelAndView modelAndView = new ModelAndView("policy-view");
        modelAndView.addObject("policies", userPolicy);

        return modelAndView;
    }

    @PatchMapping("policy/{id}/cancel")
    public String cancelPolicy(@PathVariable("id") UUID id) {

       policyService.cancelPolicy(id);

       return "redirect:/policy-view";
    }
}
