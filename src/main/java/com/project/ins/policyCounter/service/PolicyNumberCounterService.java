package com.project.ins.policyCounter.service;

import com.project.ins.policyCounter.model.PolicyNumberCounter;
import com.project.ins.policyCounter.repository.PolicyNumberCounterRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PolicyNumberCounterService {

    private final PolicyNumberCounterRepository policyNumberCounterRepository;

    public PolicyNumberCounterService(PolicyNumberCounterRepository counterRepository) {
        this.policyNumberCounterRepository = counterRepository;
    }

    @Transactional
    public synchronized String generateNextPolicyNumber() {
        PolicyNumberCounter counter = policyNumberCounterRepository.findById("policy")
                .orElseGet(() -> new PolicyNumberCounter("policy", 0L));

        long next = counter.getValue() + 1;
        counter.setValue(next);
        policyNumberCounterRepository.save(counter);

        return String.format("SG/08/%010d", next);
    }
}
