package com.project.ins.policy.repository;

import com.project.ins.policy.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, UUID> {

    List<Policy> findAllByOwner_Id(UUID ownerId);

}
