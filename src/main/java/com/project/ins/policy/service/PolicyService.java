package com.project.ins.policy.service;

import com.project.ins.exception.PolicyException;
import com.project.ins.policy.model.Policy;
import com.project.ins.policy.model.PolicyStatus;
import com.project.ins.policy.repository.PolicyRepository;
import com.project.ins.policyCounter.repository.PolicyNumberCounterRepository;
import com.project.ins.policyCounter.service.PolicyNumberCounterService;
import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.service.UserService;
import com.project.ins.web.dto.PolicyRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final PolicyNumberCounterService policyNumberCounterService;

    public PolicyService(PolicyRepository policyRepository, PolicyNumberCounterRepository policyNumberCounterRepository, PolicyNumberCounterService policyNumberCounterService, UserService userService) {
        this.policyRepository = policyRepository;
        this.policyNumberCounterService = policyNumberCounterService;
    }


    public void createPolicy(@Valid PolicyRequest policyRequest, UserData userData, User user) {

        Policy policy = Policy.builder()
                .policyNumber(policyNumberCounterService.generateNextPolicyNumber())
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
}
