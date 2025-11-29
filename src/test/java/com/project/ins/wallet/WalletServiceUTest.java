package com.project.ins.wallet;

import com.project.ins.transaction.model.Transaction;
import com.project.ins.transaction.model.TransactionStatus;
import com.project.ins.transaction.service.TransactionService;
import com.project.ins.user.model.User;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.repository.WalletRepository;
import com.project.ins.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceUTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private WalletService walletService;

    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balance(BigDecimal.valueOf(1000))
                .currency(Currency.getInstance("EUR"))
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .wallet(testWallet)
                .build();
    }

    @Test
    void createDefaultWallet_shouldCreateWalletWithDefaultBalance() {
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.createDefaultWallet();

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());

        Wallet savedWallet = walletCaptor.getValue();

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(20.00), savedWallet.getBalance());
        assertEquals(Currency.getInstance("EUR"), savedWallet.getCurrency());
        assertNotNull(savedWallet.getCreatedOn());
        assertNotNull(savedWallet.getUpdatedOn());
    }

    @Test
    void topUp_shouldAddBalanceAndCreateTransaction() {
        when(walletRepository.findByOwnerId(testUser.getId())).thenReturn(testWallet);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(transactionService).createTopTransaction(any(User.class), any(BigDecimal.class));

        walletService.topUp(testUser);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).findByOwnerId(testUser.getId());
        verify(walletRepository).save(walletCaptor.capture());
        verify(transactionService).createTopTransaction(eq(testUser), any(BigDecimal.class));

        Wallet savedWallet = walletCaptor.getValue();
        assertEquals(0, BigDecimal.valueOf(1200).compareTo(savedWallet.getBalance()));
    }

    @Test
    void reduceAmount_shouldSubtractBalanceWhenSufficientFunds() {
        BigDecimal premiumAmount = BigDecimal.valueOf(200);
        Transaction expectedTransaction = Transaction.builder()
                .status(TransactionStatus.SUCCESS)
                .build();

        when(walletRepository.findByOwnerId(testUser.getId())).thenReturn(testWallet);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionService.createWithdrawalTransaction(eq(testUser), any(BigDecimal.class), eq(premiumAmount)))
                .thenReturn(expectedTransaction);

        Transaction result = walletService.reduceAmount(premiumAmount, testUser);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).findByOwnerId(testUser.getId());
        verify(walletRepository).save(walletCaptor.capture());
        verify(transactionService).createWithdrawalTransaction(eq(testUser), any(BigDecimal.class), eq(premiumAmount));

        Wallet savedWallet = walletCaptor.getValue();
        assertEquals(BigDecimal.valueOf(800), savedWallet.getBalance());
        assertEquals(expectedTransaction, result);
    }

    @Test
    void reduceAmount_shouldCreateFailTransactionWhenInsufficientFunds() {
        BigDecimal premiumAmount = BigDecimal.valueOf(2000);
        Transaction expectedTransaction = Transaction.builder()
                .status(TransactionStatus.FAILED)
                .build();

        when(walletRepository.findByOwnerId(testUser.getId())).thenReturn(testWallet);
        when(transactionService.createFailTransaction(eq(testUser), eq(testWallet.getBalance()), eq(premiumAmount), anyString()))
                .thenReturn(expectedTransaction);

        Transaction result = walletService.reduceAmount(premiumAmount, testUser);

        verify(walletRepository).findByOwnerId(testUser.getId());
        verify(transactionService).createFailTransaction(eq(testUser), eq(testWallet.getBalance()), eq(premiumAmount), eq("insufficient balance"));
        verify(walletRepository, never()).save(any(Wallet.class));

        assertEquals(expectedTransaction, result);
    }

    @Test
    void findByOwnerId_shouldReturnWallet() {
        when(walletRepository.findByOwnerId(testUser.getId())).thenReturn(testWallet);

        Wallet result = walletService.findByOwnerId(testUser.getId());

        verify(walletRepository).findByOwnerId(testUser.getId());
        assertEquals(testWallet, result);
    }

    @Test
    void save_shouldSaveWallet() {
        when(walletRepository.save(testWallet)).thenReturn(testWallet);

        walletService.save(testWallet);

        verify(walletRepository).save(testWallet);
    }
}
