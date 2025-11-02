package com.project.ins.wallet.service;

import com.project.ins.transaction.model.Transaction;
import com.project.ins.transaction.service.TransactionService;
import com.project.ins.user.model.User;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Slf4j
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public Wallet createDefaultWallet() {
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(20.00))
                .currency(Currency.getInstance("EUR"))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        return walletRepository.save(wallet);
    }

    public void topUp(User user) {

        Wallet wallet = walletRepository.findByOwnerId(user.getId());
        wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(200.00)));
        walletRepository.save(wallet);
        transactionService.createTopTransaction(user, wallet.getBalance());
        log.info("Wallet Top Up Successfully");
    }

    public Transaction reduceAmount(BigDecimal premiumAmount, User user) {

        Wallet wallet = walletRepository.findByOwnerId(user.getId());
        if (wallet.getBalance().compareTo(premiumAmount) >= 0) {
            wallet.setBalance(wallet.getBalance().subtract(premiumAmount));
            walletRepository.save(wallet);
            return transactionService.createWithdrawalTransaction(user, wallet.getBalance(), premiumAmount);
        }else{
            return transactionService.createFailTransaction(user, wallet.getBalance(), premiumAmount, "insufficient balance");
        }

    }
}
