package com.project.ins.transaction.service;

import com.project.ins.transaction.model.Transaction;
import com.project.ins.transaction.model.TransactionStatus;
import com.project.ins.transaction.model.TransactionType;
import com.project.ins.transaction.repository.TransactionRepository;
import com.project.ins.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTopTransaction(User user, BigDecimal balance) {

        Transaction transaction = Transaction.builder()
                .owner(user)
                .amount(BigDecimal.valueOf(200.00))
                .balanceLeft(balance)
                .currency(Currency.getInstance("EUR"))
                .status(TransactionStatus.SUCCESS)
                .type(TransactionType.DEPOSIT)
                .createdOn(LocalDateTime.now())
                .description("Your wallet has been credited with 200 euros.")
                .build();

        transactionRepository.save(transaction);


    }

    public List<Transaction> findAllByUserId(UUID id) {
        return transactionRepository.findAllByOwner_Id(id);
    }

    public List<Transaction> findAllByUserIdLimit(UUID id) {

        return transactionRepository.findAllByOwner_Id(id).stream().sorted(Comparator.comparing(Transaction::getCreatedOn).reversed()).limit(2).toList();

    }
}
