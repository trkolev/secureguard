package com.project.ins.policy.service;

import com.project.ins.client.NumberGenerator;
import com.project.ins.exception.PolicyException;
import com.project.ins.policy.model.Policy;
import com.project.ins.policy.model.PolicyStatus;
import com.project.ins.policy.repository.PolicyRepository;
import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.web.dto.PolicyRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PolicyService {

    @Autowired
    private final PolicyRepository policyRepository;
    private final NumberGenerator numberGenerator;

    public PolicyService(PolicyRepository policyRepository, NumberGenerator numberGenerator) {
        this.policyRepository = policyRepository;
        this.numberGenerator = numberGenerator;
    }


    public void createPolicy(@Valid PolicyRequest policyRequest, UserData userData, User user) {

        if(user.getWallet().getBalance().compareTo(policyRequest.getPremiumAmount()) >= 0) {

            Policy policy = Policy.builder()
                    .policyNumber(numberGenerator.getResponse())
                    .owner(user)
                    .policyName(policyRequest.getPolicyName())
                    .startDate(policyRequest.getStartDate())
                    .endDate(policyRequest.getEndDate())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .coverageDescription(policyRequest.getCoverageDescription())
                    .premiumAmount(policyRequest.getPremiumAmount())
                    .coverageAmount(policyRequest.getCoverageAmount())
                    .status(PolicyStatus.ACTIVE)
                    .build();

            policyRepository.save(policy);
        }else  {
            throw new PolicyException("Insufficient wallet balance");
        }
    }

    public List<Policy> getAllByUserId(UUID id) {

        return policyRepository.findAllByOwner_Id(id);

    }

    public  List<Policy> getAllByOwnerIdLimited(UUID id) {

        List<Policy> allByOwnerId = policyRepository.findAllByOwner_Id(id);

        return allByOwnerId.stream().sorted(Comparator.comparing(Policy::getCreatedAt).reversed()).limit(3).toList();
    }


    public void cancelPolicy(UUID id) {

        Optional<Policy> policy = policyRepository.findById(id);
        if(policy.isEmpty()) {
            throw new PolicyException("Policy not found");
        }

        policy.get().setStatus(PolicyStatus.CANCELLED);
        policy.get().setCancellationDate(LocalDate.now());
        policyRepository.save(policy.get());

    }

    public BigDecimal findTotalCoverage(UUID id) {

        return getAllByUserId(id).stream()
                .map(Policy::getCoverageAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    public BigDecimal findTotalPremium(UUID id) {

        return getAllByUserId(id).stream()
                .map(Policy::getPremiumAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
}
