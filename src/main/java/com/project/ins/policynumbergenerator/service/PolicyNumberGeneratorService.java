package com.project.ins.policynumbergenerator.service;

import com.project.ins.policynumbergenerator.model.PolicyNumberGenerator;
import com.project.ins.policynumbergenerator.repository.PolicyNumberGeneratorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PolicyNumberGeneratorService {

    private final PolicyNumberGeneratorRepository policyNumberGeneratorRepository;

    public PolicyNumberGeneratorService(PolicyNumberGeneratorRepository counterRepository) {
        this.policyNumberGeneratorRepository = counterRepository;
    }

    @Transactional
    public synchronized String generateNextPolicyNumber() {
        PolicyNumberGenerator counter = policyNumberGeneratorRepository.findById("policy")
                .orElseGet(() -> new PolicyNumberGenerator("policy", 0L));

        long next = counter.getValue() + 1;
        counter.setValue(next);
        policyNumberGeneratorRepository.save(counter);

        return String.format("SG/08/%010d", next);
    }
}
