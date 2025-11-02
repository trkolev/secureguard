package com.project.ins.calimnumbergenerator.repository;

import com.project.ins.calimnumbergenerator.model.ClaimNumberGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimNumberGeneratorRepository extends JpaRepository<ClaimNumberGenerator, String> {
}
