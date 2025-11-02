package com.project.ins.calimnumbergenerator.service;

import com.project.ins.calimnumbergenerator.model.ClaimNumberGenerator;
import com.project.ins.calimnumbergenerator.repository.ClaimNumberGeneratorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ClaimNumberGeneratorService {

    private final ClaimNumberGeneratorRepository claimNumberGeneratorRepository;


    public ClaimNumberGeneratorService(ClaimNumberGeneratorRepository claimNumberGeneratorRepository) {
        this.claimNumberGeneratorRepository = claimNumberGeneratorRepository;
    }

    @Transactional
    public synchronized String generateNextClaimNumber() {
        ClaimNumberGenerator counter = claimNumberGeneratorRepository.findById("claim")
                .orElseGet(() -> new ClaimNumberGenerator("claim", 0L));

        long next = counter.getValue() + 1;
        counter.setValue(next);
        claimNumberGeneratorRepository.save(counter);

        return String.format("Cl-%010d", next);
    }
}
