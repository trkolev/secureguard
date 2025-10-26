package com.project.ins.policy.service;

import com.project.ins.policy.model.Policy;
import com.project.ins.policy.model.PolicyStatus;
import com.project.ins.policy.repository.PolicyRepository;
import com.project.ins.policyCounter.repository.PolicyNumberCounterRepository;
import com.project.ins.policyCounter.service.PolicyNumberCounterService;
import com.project.ins.security.UserData;
import com.project.ins.user.service.UserService;
import com.project.ins.web.dto.PolicyRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class PolicyService {

    @Autowired
    private final PolicyRepository policyRepository;
    private final PolicyNumberCounterService policyNumberCounterService;
    private final UserService userService;

    public PolicyService(PolicyRepository policyRepository, PolicyNumberCounterRepository policyNumberCounterRepository, PolicyNumberCounterService policyNumberCounterService, UserService userService) {
        this.policyRepository = policyRepository;
        this.policyNumberCounterService = policyNumberCounterService;
        this.userService = userService;
    }


    public void createPolicy(@Valid PolicyRequest policyRequest, UserData userData) {

        Policy policy = Policy.builder()
                .policyNumber(policyNumberCounterService.generateNextPolicyNumber())
                .owner(userService.findById(userData.getId()))
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
}
