package com.project.ins.claim.service;

import com.project.ins.calimnumbergenerator.model.ClaimNumberGenerator;
import com.project.ins.calimnumbergenerator.service.ClaimNumberGeneratorService;
import com.project.ins.claim.model.Claim;
import com.project.ins.claim.model.ClaimStatus;
import com.project.ins.claim.model.ClaimType;
import com.project.ins.claim.repository.ClaimRepository;
import com.project.ins.user.model.User;
import com.project.ins.web.dto.ClaimRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimNumberGeneratorService claimNumberGeneratorService;

    public ClaimService(ClaimRepository claimRepository, ClaimNumberGeneratorService claimNumberGeneratorService) {
        this.claimRepository = claimRepository;

        this.claimNumberGeneratorService = claimNumberGeneratorService;
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
                .claimNumber(claimNumberGeneratorService.generateNextClaimNumber())
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
//            throw   new RuntimeException("This user doesn't have any claims");
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
}
