package com.project.ins.policynumbergenerator.repository;

import com.project.ins.policynumbergenerator.model.PolicyNumberGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyNumberGeneratorRepository extends JpaRepository <PolicyNumberGenerator, String > {

}
