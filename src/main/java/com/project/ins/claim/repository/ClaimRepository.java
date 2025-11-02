package com.project.ins.claim.repository;

import com.project.ins.claim.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findAllByOwner_Id(UUID ownerId);
}
