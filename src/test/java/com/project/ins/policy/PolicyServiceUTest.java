package com.project.ins.policy;

import com.project.ins.exception.PolicyException;
import com.project.ins.numbergenerator.NumberGenerator;
import com.project.ins.policy.model.Policy;
import com.project.ins.policy.model.PolicyName;
import com.project.ins.policy.model.PolicyStatus;
import com.project.ins.policy.repository.PolicyRepository;
import com.project.ins.policy.service.PolicyService;
import com.project.ins.user.model.User;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.web.dto.PolicyRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceUTest {

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private NumberGenerator numberGenerator;

    @InjectMocks
    private PolicyService policyService;

    @Test
    void createPolicy_shouldThrowExceptionWhenUserDontHaveEnoughMoney() {
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(9))
                .build();

        User user = User.builder()
                .wallet(wallet)
                .build();

        PolicyRequest policyRequest = PolicyRequest.builder()
                .premiumAmount(BigDecimal.valueOf(10))
                .build();

        assertThrows(PolicyException.class, () -> policyService.createPolicy(policyRequest, user));

    }

    @Test
    void createPolicy_shouldCreatePolicyWhenUserHasSufficientBalance() {

        String expectedPolicyNumber = "POL-12345";
        BigDecimal walletBalance = BigDecimal.valueOf(1000);
        BigDecimal premiumAmount = BigDecimal.valueOf(500);
        BigDecimal coverageAmount = BigDecimal.valueOf(10000);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);
        String coverageDescription = "Test coverage description";
        PolicyName policyName = PolicyName.VEHICLE;
        UUID userId = UUID.randomUUID();

        Wallet wallet = Wallet.builder()
                .balance(walletBalance)
                .build();

        User user = User.builder()
                .id(userId)
                .wallet(wallet)
                .build();

        PolicyRequest policyRequest = PolicyRequest.builder()
                .policyName(policyName)
                .startDate(startDate)
                .endDate(endDate)
                .coverageDescription(coverageDescription)
                .coverageAmount(coverageAmount)
                .premiumAmount(premiumAmount)
                .build();

        when(numberGenerator.getResponse()).thenReturn(expectedPolicyNumber);
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        policyService.createPolicy(policyRequest, user);

        verify(numberGenerator).getResponse();

        ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);
        verify(policyRepository).save(policyCaptor.capture());

        Policy savedPolicy = policyCaptor.getValue();

        assertNotNull(savedPolicy);
        assertEquals(expectedPolicyNumber, savedPolicy.getPolicyNumber());
        assertEquals(user, savedPolicy.getOwner());
        assertEquals(policyName, savedPolicy.getPolicyName());
        assertEquals(startDate, savedPolicy.getStartDate());
        assertEquals(endDate, savedPolicy.getEndDate());
        assertEquals(coverageDescription, savedPolicy.getCoverageDescription());
        assertEquals(premiumAmount, savedPolicy.getPremiumAmount());
        assertEquals(coverageAmount, savedPolicy.getCoverageAmount());
        assertEquals(PolicyStatus.ACTIVE, savedPolicy.getStatus());
        assertNotNull(savedPolicy.getCreatedAt());
        assertNotNull(savedPolicy.getUpdatedAt());
    }

    @Test
    void createPolicy_shouldCreatePolicyWhenBalanceEqualsPremiumAmount() {

        String expectedPolicyNumber = "POL-67890";
        BigDecimal balance = BigDecimal.valueOf(500);
        BigDecimal premiumAmount = BigDecimal.valueOf(500);

        Wallet wallet = Wallet.builder()
                .balance(balance)
                .build();

        User user = User.builder()
                .wallet(wallet)
                .build();

        PolicyRequest policyRequest = PolicyRequest.builder()
                .policyName(PolicyName.PERSON)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .coverageDescription("Life insurance coverage")
                .coverageAmount(BigDecimal.valueOf(50000))
                .premiumAmount(premiumAmount)
                .build();

        when(numberGenerator.getResponse()).thenReturn(expectedPolicyNumber);
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        policyService.createPolicy(policyRequest, user);

        ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);
        verify(policyRepository).save(policyCaptor.capture());

        Policy savedPolicy = policyCaptor.getValue();
        assertEquals(expectedPolicyNumber, savedPolicy.getPolicyNumber());
        assertEquals(PolicyStatus.ACTIVE, savedPolicy.getStatus());
    }

    @Test
    void cancelPolicy_shouldThrowExceptionWhenPolicyDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        when(policyRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(PolicyException.class, () -> policyService.cancelPolicy(uuid));
    }

    @Test
    void cancelPolicy_shouldSetStatusToCancelled() {
        UUID uuid = UUID.randomUUID();
        Policy policy = Policy.builder()
                .id(uuid)
                .status(PolicyStatus.ACTIVE)
                .build();

        when(policyRepository.findById(uuid)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));
        policyService.cancelPolicy(uuid);
        ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);
        verify(policyRepository).save(policyCaptor.capture());

        assertEquals(PolicyStatus.CANCELLED, policyCaptor.getValue().getStatus());
    }

    @Test
    void findToralCoverage_shouldReturnToralCoverage() {

        UUID uuid = UUID.randomUUID();

        Policy policy1 = Policy.builder()
                .coverageAmount(BigDecimal.valueOf(100))
                .build();

        Policy policy2 = Policy.builder()
                .coverageAmount(BigDecimal.valueOf(200))
                .build();

        Policy policy3 = Policy.builder()
                .coverageAmount(BigDecimal.valueOf(300))
                .build();

        when(policyRepository.findAllByOwner_Id(uuid)).thenReturn(List.of(policy1, policy2, policy3));

        BigDecimal totalCoverage = policyService.findTotalCoverage(uuid);

        assertEquals(BigDecimal.valueOf(600), totalCoverage);
    }

    @Test
    void findTotalPremium_shouldReturnTotalPremium() {
        UUID uuid = UUID.randomUUID();

        Policy policy1 = Policy.builder()
                .premiumAmount(BigDecimal.valueOf(100))
                .build();

        Policy policy2 = Policy.builder()
                .premiumAmount(BigDecimal.valueOf(200))
                .build();

        Policy policy3 = Policy.builder()
                .premiumAmount(BigDecimal.valueOf(300))
                .build();

        when(policyRepository.findAllByOwner_Id(uuid)).thenReturn(List.of(policy1, policy2, policy3));

        BigDecimal totalPremium = policyService.findTotalPremium(uuid);

        assertEquals(BigDecimal.valueOf(600), totalPremium);
    }
}
