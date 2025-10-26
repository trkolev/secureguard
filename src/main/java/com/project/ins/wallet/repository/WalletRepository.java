package com.project.ins.wallet.repository;

import com.project.ins.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Wallet findByOwnerId(UUID ownerId);

}
