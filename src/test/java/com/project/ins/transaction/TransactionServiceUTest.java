package com.project.ins.transaction;

import com.project.ins.transaction.model.Transaction;
import com.project.ins.transaction.model.TransactionStatus;
import com.project.ins.transaction.model.TransactionType;
import com.project.ins.transaction.repository.TransactionRepository;
import com.project.ins.transaction.service.TransactionService;
import com.project.ins.user.model.User;
import com.project.ins.wallet.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceUTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testWallet = Wallet.builder()
                .balance(BigDecimal.valueOf(1000))
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .wallet(testWallet)
                .build();
    }

    @Test
    void createTopTransaction_shouldCreateDepositTransaction() {
        BigDecimal balance = BigDecimal.valueOf(1000);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.createTopTransaction(testUser, balance);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();

        assertNotNull(savedTransaction);
        assertEquals(testUser, savedTransaction.getOwner());
        assertEquals(BigDecimal.valueOf(200.00), savedTransaction.getAmount());
        assertEquals(balance, savedTransaction.getBalanceLeft());
        assertEquals("EUR", savedTransaction.getCurrency());
        assertEquals(TransactionStatus.SUCCESS, savedTransaction.getStatus());
        assertEquals(TransactionType.DEPOSIT, savedTransaction.getType());
        assertEquals("Your wallet has been credited with 200 euros.", savedTransaction.getDescription());
        assertNotNull(savedTransaction.getCreatedOn());
    }

    @Test
    void findAllByUserId_shouldReturnSortedTransactions() {
        UUID userId = UUID.randomUUID();
        Transaction transaction1 = Transaction.builder()
                .createdOn(LocalDateTime.now().minusDays(1))
                .build();
        Transaction transaction2 = Transaction.builder()
                .createdOn(LocalDateTime.now().minusDays(2))
                .build();
        Transaction transaction3 = Transaction.builder()
                .createdOn(LocalDateTime.now())
                .build();

        when(transactionRepository.findAllByOwner_Id(userId))
                .thenReturn(Arrays.asList(transaction1, transaction2, transaction3));

        List<Transaction> result = transactionService.findAllByUserId(userId);

        verify(transactionRepository).findAllByOwner_Id(userId);
        assertEquals(3, result.size());
        assertEquals(transaction3, result.get(0));
        assertEquals(transaction1, result.get(1));
        assertEquals(transaction2, result.get(2));
    }

    @Test
    void findAllByUserIdLimit_shouldReturnLimitedSortedTransactions() {
        UUID userId = UUID.randomUUID();
        Transaction transaction1 = Transaction.builder()
                .createdOn(LocalDateTime.now().minusDays(1))
                .build();
        Transaction transaction2 = Transaction.builder()
                .createdOn(LocalDateTime.now().minusDays(2))
                .build();
        Transaction transaction3 = Transaction.builder()
                .createdOn(LocalDateTime.now())
                .build();

        when(transactionRepository.findAllByOwner_Id(userId))
                .thenReturn(Arrays.asList(transaction1, transaction2, transaction3));

        List<Transaction> result = transactionService.findAllByUserIdLimit(userId);

        verify(transactionRepository).findAllByOwner_Id(userId);
        assertEquals(2, result.size());
        assertEquals(transaction3, result.get(0));
        assertEquals(transaction1, result.get(1));
    }

    @Test
    void findAllByUserIdLimit_shouldReturnEmptyListWhenNoTransactions() {
        UUID userId = UUID.randomUUID();

        when(transactionRepository.findAllByOwner_Id(userId)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.findAllByUserIdLimit(userId);

        verify(transactionRepository).findAllByOwner_Id(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void createWithdrawalTransaction_shouldCreateWithdrawTransaction() {
        BigDecimal balance = BigDecimal.valueOf(1000);
        BigDecimal amount = BigDecimal.valueOf(200);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createWithdrawalTransaction(testUser, balance, amount);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();

        assertNotNull(result);
        assertEquals(testUser, savedTransaction.getOwner());
        assertEquals(amount, savedTransaction.getAmount());
        assertEquals(BigDecimal.valueOf(800), savedTransaction.getBalanceLeft());
        assertEquals("EUR", savedTransaction.getCurrency());
        assertEquals(TransactionStatus.SUCCESS, savedTransaction.getStatus());
        assertEquals(TransactionType.WITHDRAW, savedTransaction.getType());
        assertEquals("You successfully pay your premium", savedTransaction.getDescription());
        assertNotNull(savedTransaction.getCreatedOn());
    }

    @Test
    void createFailTransaction_shouldCreateFailedTransaction() {
        BigDecimal balance = BigDecimal.valueOf(100);
        BigDecimal amount = BigDecimal.valueOf(200);
        String failureReason = "insufficient balance";

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createFailTransaction(testUser, balance, amount, failureReason);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();

        assertNotNull(result);
        assertEquals(testUser, savedTransaction.getOwner());
        assertEquals(amount, savedTransaction.getAmount());
        assertEquals(balance, savedTransaction.getBalanceLeft());
        assertEquals("EUR", savedTransaction.getCurrency());
        assertEquals(TransactionStatus.FAILED, savedTransaction.getStatus());
        assertEquals(TransactionType.WITHDRAW, savedTransaction.getType());
        assertEquals("Premium payment failed", savedTransaction.getDescription());
        assertEquals(failureReason, savedTransaction.getFailureReason());
        assertNotNull(savedTransaction.getCreatedOn());
    }

    @Test
    void claimPaymentTransaction_shouldCreateDepositTransaction() {
        BigDecimal amount = BigDecimal.valueOf(500);
        BigDecimal balance = BigDecimal.valueOf(1500);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.claimPaymentTransaction(testUser, amount, balance);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();

        assertNotNull(savedTransaction);
        assertEquals(testUser, savedTransaction.getOwner());
        assertEquals(amount, savedTransaction.getAmount());
        assertEquals(balance, savedTransaction.getBalanceLeft());
        assertEquals("EUR", savedTransaction.getCurrency());
        assertEquals(TransactionStatus.SUCCESS, savedTransaction.getStatus());
        assertEquals(TransactionType.DEPOSIT, savedTransaction.getType());
        assertEquals("Claim payment", savedTransaction.getDescription());
        assertNotNull(savedTransaction.getCreatedOn());
    }
}
