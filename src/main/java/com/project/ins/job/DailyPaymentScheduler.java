package com.project.ins.job;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.model.ClaimStatus;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.notification.service.NotificationService;
import com.project.ins.transaction.service.TransactionService;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class DailyPaymentScheduler {

    private final WalletService walletService;
    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final ClaimService claimService;

    public DailyPaymentScheduler(WalletService walletService, NotificationService notificationService, TransactionService transactionService, ClaimService claimService) {
        this.walletService = walletService;
        this.notificationService = notificationService;
        this.transactionService = transactionService;
        this.claimService = claimService;
    }

    @Scheduled(cron = "0 47 13 * * *")
    @Transactional
    public void dailyPayments() {
        List<Claim> claims = claimService.findAllApproved();

        for (Claim claim : claims) {
            Wallet wallet = claim.getOwner().getWallet();
            wallet.setBalance(wallet.getBalance().add(claim.getAmount()));
            wallet.setUpdatedOn(LocalDateTime.now());
            walletService.save(wallet);

            claim.setStatus(ClaimStatus.PAID);
            claim.setUpdatedDate(LocalDateTime.now());
            claimService.save(claim);

            String phone = claim.getOwner().getPhoneNumber();

            if (phone == null) {
                log.warn("User with id: {} has no phone number. Skipping SMS for claim {}",
                        claim.getOwner().getId(),
                        claim.getClaimNumber());
            } else {
                notificationService.sendNotification(phone,
                        String.format("SecureGuard: Your claim #%s has been processed. We’ve sent €%s to your account. Thank you for choosing as.", claim.getClaimNumber(), claim.getAmount().toPlainString()),
                        claim.getOwner().getId());
            }

            transactionService.claimPaymentTransaction(claim.getOwner(), claim.getAmount(), wallet.getBalance());

            log.info("Claim {} successfully paid", claim.getClaimNumber());
        }
    }

}
