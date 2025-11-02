package com.project.ins.web;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.repository.ClaimRepository;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.policy.model.Policy;
import com.project.ins.policy.service.PolicyService;
import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import com.project.ins.web.dto.ClaimRequest;
import jakarta.validation.Valid;
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
public class ClaimController {

    private final PolicyService policyService;
    private final ClaimService claimService;
    private final UserService userService;

    public ClaimController(PolicyService policyService, ClaimRepository claimRepository, ClaimService claimService, UserService userService) {
        this.policyService = policyService;

        this.claimService = claimService;
        this.userService = userService;
    }

    @GetMapping("/claims")
    public ModelAndView createClaim(@AuthenticationPrincipal UserData userData) {

        List<Policy> policies = policyService.getAllByUserId(userData.getId());

        ModelAndView modelAndView = new ModelAndView("claim");
        modelAndView.addObject("claimRequest", new ClaimRequest());
        modelAndView.addObject("policies", policies);


        return modelAndView;
    }

    @PostMapping("/claims")
    private ModelAndView createClaim(@Valid ClaimRequest claimRequest,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserData userData,
                                     RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("claimRequest", claimRequest);
            return new ModelAndView("claim");
        }

        User user = userService.findById(userData.getId());
        Claim claim = claimService.create(claimRequest, user);
        redirectAttributes.addFlashAttribute("successMessage", "Claim created successfully");

        return new ModelAndView("redirect:/claims");
    }

    @GetMapping("/claims/all")
    public ModelAndView getAllClaims(@AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("claim-view");
        List<Claim> claims = claimService.findAllByOwnerId(userData.getId());
        modelAndView.addObject("claims", claims);

        return modelAndView;
    }

    @PatchMapping("/claims/{id}/cancel")
    public String cancelClaim(@AuthenticationPrincipal UserData userData, @PathVariable UUID id, RedirectAttributes redirectAttributes) {

        claimService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessage", "Claim canceled successfully");

        return "redirect:/claims/all";
    }

}
