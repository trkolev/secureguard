package com.project.ins.web;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.security.UserData;
import com.project.ins.web.dto.ClaimLiquidationRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class EmployeeController {

    private final ClaimService claimService;

    public EmployeeController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/employee")
    public ModelAndView getEmployee() {


        List<Claim> claims = claimService.findAll();
        ModelAndView modelAndView = new ModelAndView("employee");
        modelAndView.addObject("claims", claims);
        modelAndView.addObject("liquidRequest", new ClaimLiquidationRequest());

        return modelAndView;
    }

    @PatchMapping("/employee/claims/{id}/approve")
    public String approveClaim(@PathVariable UUID id, ClaimLiquidationRequest request, @AuthenticationPrincipal UserData userData, RedirectAttributes redirectAttributes) {

        claimService.approveClaim(id, request, userData.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Claim approved successfully");

        return "redirect:/employee";
    }

    @PatchMapping("/employee/claims/{id}/decline")
    public String declineClaim(@PathVariable UUID id, ClaimLiquidationRequest request, RedirectAttributes redirectAttributes) {

        claimService.declineClaim(id, request);

        redirectAttributes.addFlashAttribute("successMessage", "Claim declined successfully");

        return "redirect:/employee";
    }
}
