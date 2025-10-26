package com.project.ins.web;

import com.project.ins.policy.service.PolicyService;
import com.project.ins.security.UserData;
import com.project.ins.web.dto.PolicyRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PolicyController {

    private PolicyService policyService;

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

        policyService.createPolicy(policyRequest, userData);
        redirectAttributes.addFlashAttribute("successMessage", "Policy created successfully");

        return new ModelAndView("redirect:/policy");
    }
}
