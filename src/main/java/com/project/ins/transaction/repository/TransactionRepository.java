package com.project.ins.transaction.repository;

import com.project.ins.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByOwner_Id(UUID ownerId);
}
