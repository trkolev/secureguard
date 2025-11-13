package com.project.ins.claim.service;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.model.ClaimStatus;
import com.project.ins.claim.model.ClaimType;
import com.project.ins.claim.repository.ClaimRepository;
import com.project.ins.client.NumberGenerator;
import com.project.ins.exception.ClaimNotFoundException;
import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.service.WalletService;
import com.project.ins.web.dto.ClaimLiquidationRequest;
import com.project.ins.web.dto.ClaimRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final NumberGenerator numberGenerator;
    private final WalletService walletService;

    public ClaimService(ClaimRepository claimRepository, NumberGenerator numberGenerator, WalletService walletService) {
        this.claimRepository = claimRepository;

        this.numberGenerator = numberGenerator;
        this.walletService = walletService;
    }


    public Claim create(@Valid ClaimRequest claimRequest, User user) {

        ClaimType claimType;

        if (claimRequest.getClientPolicy().getPolicyName().getName().equals("Life insurance")) {
            claimType = ClaimType.LIFE;
        }else if (claimRequest.getClientPolicy().getPolicyName().getName().equals("Vehicle insurance")) {
            claimType = ClaimType.VEHICLE;
        }else{
            claimType = ClaimType.HOME;
        }

        Claim claim = Claim.builder()
                .owner(user)
                .policy(claimRequest.getClientPolicy())
                .claimType(claimType)
                .claimNumber(numberGenerator.getClaimNumbers())
                .eventDate(claimRequest.getIncidentDate())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .eventDescription(claimRequest.getDescription())
                .status(ClaimStatus.REGISTERED)
                .build();

        return claimRepository.save(claim);
    }


    public List<Claim> findAllByOwnerId(UUID id) {
        List<Claim> allByOwnerId = claimRepository.findAllByOwner_Id(id);
        if (allByOwnerId.isEmpty()) {
            log.info("Claim findAllByOwnerId returned empty list");
        }
        return allByOwnerId;
    }

    public void cancel(UUID id) {

        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setStatus(ClaimStatus.DECLINED);
        claimRepository.save(claim);

    }

    public List<Claim> findAllByOwnerIdLimit(UUID id) {

        List<Claim> allByOwnerId = claimRepository.findAllByOwner_Id(id);
        return allByOwnerId.stream().sorted(Comparator.comparing(Claim::getCreatedDate)).limit(3).toList();

    }

    public int findClaimsThisYear(UUID id) {

        List<Claim> claimList = findAllByOwnerId(id).stream().filter(claim -> claim.getCreatedDate().getYear() == LocalDateTime.now().getYear()).toList();

        return claimList.size();
    }

    public List<Claim> upcomingPayments(UUID id) {

        return findAllByOwnerId(id).stream().sorted(Comparator.comparing(Claim::getCreatedDate)).filter(claim -> claim.getStatus() == ClaimStatus.APPROVED).toList();

    }

    public List<Claim> upcomingPaymentsLimit(UUID id) {

        return findAllByOwnerId(id).stream().sorted(Comparator.comparing(Claim::getCreatedDate)).filter(claim -> claim.getStatus() == ClaimStatus.APPROVED).limit(3).toList();

    }

    public List<Claim> findAll() {
        return claimRepository.findAll().stream().sorted(Comparator.comparing(Claim::getCreatedDate)).toList();
    }

    public void approveClaim(UUID claimId, ClaimLiquidationRequest request, UUID userId) {

        Optional<Claim> optionalClaim = claimRepository.findById(claimId);
        if (optionalClaim.isEmpty()) {
            throw new ClaimNotFoundException();
        }

        Claim claim = optionalClaim.get();
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setDeclineReason(request.getDeclineReason());
        claim.setUpdatedDate(LocalDateTime.now());
        claim.setAmount(request.getAmount());
        claimRepository.save(claim);

    }

    public void declineClaim(UUID id, ClaimLiquidationRequest request) {

        Optional<Claim> optionalClaim = claimRepository.findById(id);
        if (optionalClaim.isEmpty()) {
            throw new ClaimNotFoundException();
        }

        Claim claim = optionalClaim.get();
        claim.setStatus(ClaimStatus.DECLINED);
        claim.setDeclineReason(request.getDeclineReason());
        claim.setUpdatedDate(LocalDateTime.now());
        claimRepository.save(claim);

    }

    @Transactional
    public void dailyPayments(){

        List<Claim> claims = claimRepository.findAll().stream().filter(claim -> claim.getStatus() == ClaimStatus.APPROVED).toList();

        for (Claim claim : claims) {

            Wallet wallet = claim.getOwner().getWallet();
            wallet.setBalance(wallet.getBalance().add(claim.getAmount()));
            wallet.setUpdatedOn(LocalDateTime.now());
            walletService.save(wallet);

            claim.setStatus(ClaimStatus.PAID);
            claim.setUpdatedDate(LocalDateTime.now());
            claimRepository.save(claim);

        }
    }
}
