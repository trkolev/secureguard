package com.project.ins.policyCounter.repository;

import com.project.ins.policyCounter.model.PolicyNumberCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyNumberCounterRepository extends JpaRepository <PolicyNumberCounter, String > {

}
