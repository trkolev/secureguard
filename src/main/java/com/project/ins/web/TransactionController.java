package com.project.ins.web;

import com.project.ins.security.UserData;
import com.project.ins.transaction.model.Transaction;
import com.project.ins.transaction.service.TransactionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @GetMapping("/transactions")
    public ModelAndView transactions(@AuthenticationPrincipal UserData userData) {

        List<Transaction> transactions = transactionService.findAllByUserId(userData.getId());

        ModelAndView modelAndView = new ModelAndView("transaction-view");
        modelAndView.addObject("transactions", transactions);

        return modelAndView;
    }

}
