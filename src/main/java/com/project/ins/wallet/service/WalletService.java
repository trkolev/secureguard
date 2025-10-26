package com.project.ins.wallet.service;

import com.project.ins.user.model.User;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Service
public class WalletService {

private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getWalletByUserId(UUID userId) {
        return walletRepository.findByOwnerId(userId);
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
}
