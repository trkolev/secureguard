package com.project.ins.claim;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.model.ClaimStatus;
import com.project.ins.claim.model.ClaimType;
import com.project.ins.claim.repository.ClaimRepository;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.exception.ClaimNotFoundException;
import com.project.ins.numbergenerator.NumberGenerator;
import com.project.ins.policy.model.Policy;
import com.project.ins.policy.model.PolicyName;
import com.project.ins.user.model.User;
import com.project.ins.web.dto.ClaimLiquidationRequest;
import com.project.ins.web.dto.ClaimRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceUTest {

    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private NumberGenerator numberGenerator;

    @InjectMocks
    private ClaimService claimService;

    @Test
    void create_shouldSetClaimTypeToLifeIfLifeInsuranceIsSelected(){

        Policy policy = Policy.builder()
                .policyName(PolicyName.PERSON)
                .build();

        ClaimRequest claimRequest = ClaimRequest.builder()
                .incidentDate(LocalDateTime.now())
                .description("description")
                .clientPolicy(policy)
                .build();

        User user = new User();

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);

        when(numberGenerator.getClaimNumbers()).thenReturn("CL00000001");
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));

        claimService.create(claimRequest, user);

        verify(claimRepository).save(captor.capture());
        Claim savedClaim = captor.getValue();
        assertEquals(ClaimType.LIFE, savedClaim.getClaimType());
    }

    @Test
    void create_shouldSetClaimTypeToVehicleIfVehicleInsuranceIsSelected() {
        Policy policy = Policy.builder()
                .policyName(PolicyName.VEHICLE)
                .build();

        ClaimRequest claimRequest = ClaimRequest.builder()
                .incidentDate(LocalDateTime.now())
                .description("vehicle accident")
                .clientPolicy(policy)
                .build();

        User user = new User();

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);

        when(numberGenerator.getClaimNumbers()).thenReturn("CL00000002");
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));

        claimService.create(claimRequest, user);

        verify(claimRepository).save(captor.capture());
        Claim savedClaim = captor.getValue();
        assertEquals(ClaimType.VEHICLE, savedClaim.getClaimType());
    }

    @Test
    void create_shouldSetClaimTypeToHomeIfPropertyInsuranceIsSelected() {
        Policy policy = Policy.builder()
                .policyName(PolicyName.PROPERTY)
                .build();

        ClaimRequest claimRequest = ClaimRequest.builder()
                .incidentDate(LocalDateTime.now())
                .description("property damage")
                .clientPolicy(policy)
                .build();

        User user = new User();

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);

        when(numberGenerator.getClaimNumbers()).thenReturn("CL00000003");
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));

        claimService.create(claimRequest, user);

        verify(claimRepository).save(captor.capture());
        Claim savedClaim = captor.getValue();
        assertEquals(ClaimType.HOME, savedClaim.getClaimType());
    }

    @Test
    void findAllByOwnerId_shouldReturnClaims(){

        UUID ownerId = UUID.randomUUID();
        Claim claim1 = Claim.builder().build();
        Claim claim2 = Claim.builder().build();

        List<Claim> claims = Arrays.asList(claim1, claim2);

        when(claimRepository.findAllByOwner_Id(ownerId)).thenReturn(claims);
        List<Claim> foundClaims = claimService.findAllByOwnerId(ownerId);
        assertEquals(claims, foundClaims);

    }

    @Test
    void findByOwnerId_shouldLogWhenClaimIsNotFound(){
        UUID ownerId = UUID.randomUUID();

        when(claimRepository.findAllByOwner_Id(ownerId)).thenReturn(List.of());

        List<Claim> foundClaims = claimService.findAllByOwnerId(ownerId);

        assertThat(foundClaims).isEmpty();
    }

    @Test
    void approveClaim_shouldThrowExceptionWhenClaimIsNotFound(){
        UUID claimId = UUID.randomUUID();
        ClaimLiquidationRequest claimLiquidationRequest = new ClaimLiquidationRequest();
        when(claimRepository.findById(claimId)).thenReturn(Optional.empty());

        assertThrows(ClaimNotFoundException.class, () -> claimService.approveClaim(claimId, claimLiquidationRequest));
    }

    @Test
    void approveClaim_shouldSaveClaimWithClaimLiquidationRequestInformation(){
        UUID claimId = UUID.randomUUID();
        ClaimLiquidationRequest claimLiquidationRequest = ClaimLiquidationRequest.builder()
                .amount(BigDecimal.valueOf(500))
                .declineReason("It is test")
                .build();

        Claim claim = Claim.builder()
                .status(ClaimStatus.REGISTERED)
                .declineReason("")
                .updatedDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(100))
                .build();

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));
        claimService.approveClaim(claimId, claimLiquidationRequest);
        verify(claimRepository).save(captor.capture());
        Claim savedClaim = captor.getValue();

        assertEquals(ClaimStatus.APPROVED, savedClaim.getStatus());
        assertEquals(claimLiquidationRequest.getAmount(), savedClaim.getAmount());
        assertEquals(claimLiquidationRequest.getDeclineReason(), savedClaim.getDeclineReason());
    }

    @Test
    void declineClaim_shouldThrowExceptionWhenClaimIsNotFound(){
        UUID claimId = UUID.randomUUID();
        ClaimLiquidationRequest claimLiquidationRequest = new ClaimLiquidationRequest();
        when(claimRepository.findById(claimId)).thenReturn(Optional.empty());

        assertThrows(ClaimNotFoundException.class, () -> claimService.declineClaim(claimId, claimLiquidationRequest));
    }

    @Test
    void declineClaim_shouldSaveClaimWithClaimLiquidationRequestInformation(){
        UUID claimId = UUID.randomUUID();
        ClaimLiquidationRequest claimLiquidationRequest = ClaimLiquidationRequest.builder()
                .amount(BigDecimal.valueOf(500))
                .declineReason("It is test")
                .build();

        Claim claim = Claim.builder()
                .status(ClaimStatus.REGISTERED)
                .declineReason("")
                .updatedDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(100))
                .build();

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));
        claimService.declineClaim(claimId, claimLiquidationRequest);
        verify(claimRepository).save(captor.capture());
        Claim savedClaim = captor.getValue();

        assertEquals(ClaimStatus.DECLINED, savedClaim.getStatus());
        assertEquals(claimLiquidationRequest.getDeclineReason(), savedClaim.getDeclineReason());
    }

    @Test
    void cancel_shouldThrowExceptionWhenClaimIsNotFound(){
        UUID claimId = UUID.randomUUID();
        when(claimRepository.findById(claimId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> claimService.cancel(claimId));
    }

    @Test
    void cancel_shouldChangeStatusOfClaimAndSaveIt(){
        UUID claimId = UUID.randomUUID();

        Claim claim = Claim.builder()
                .status(ClaimStatus.REGISTERED)
                .build();

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        claimService.cancel(claimId);

        assertEquals(ClaimStatus.DECLINED, claim.getStatus());

    }

    @Test
    void findClaimsThisYear_shouldReturnOnlyClaimsFromCurrentYear() {
        UUID ownerId = UUID.randomUUID();
        int currentYear = LocalDateTime.now().getYear();

        Claim claimThisYear1 = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear, 6, 15, 10, 0))
                .build();

        Claim claimThisYear2 = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear, 11, 20, 14, 30))
                .build();

        Claim claimLastYear = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear - 1, 12, 31, 23, 59))
                .build();

        List<Claim> allClaims = Arrays.asList(claimThisYear1, claimLastYear, claimThisYear2);

        when(claimRepository.findAllByOwner_Id(ownerId)).thenReturn(allClaims);

        int result = claimService.findClaimsThisYear(ownerId);

        assertEquals(2, result);
    }

    @Test
    void findClaimsThisYear_shouldReturnZeroWhenNoClaimsFromCurrentYear() {
        UUID ownerId = UUID.randomUUID();
        int currentYear = LocalDateTime.now().getYear();

        Claim claimLastYear = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear - 1, 6, 15, 10, 0))
                .build();

        Claim claimTwoYearsAgo = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear - 2, 3, 10, 8, 0))
                .build();

        List<Claim> allClaims = Arrays.asList(claimLastYear, claimTwoYearsAgo);

        when(claimRepository.findAllByOwner_Id(ownerId)).thenReturn(allClaims);

        int result = claimService.findClaimsThisYear(ownerId);

        assertEquals(0, result);
    }

    @Test
    void findClaimsThisYear_shouldReturnZeroWhenNoClaimsExist() {
        UUID ownerId = UUID.randomUUID();

        when(claimRepository.findAllByOwner_Id(ownerId)).thenReturn(List.of());

        int result = claimService.findClaimsThisYear(ownerId);

        assertEquals(0, result);
    }

    @Test
    void findAllByOwnerIdLimit_shouldReturnLimitedAndSortedClaims() {
        UUID ownerId = UUID.randomUUID();
        int currentYear = LocalDateTime.now().getYear();

        Claim claim1 = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear, 3, 15, 10, 0))
                .build();

        Claim claim2 = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear, 1, 10, 8, 0))
                .build();

        Claim claim3 = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear, 6, 20, 14, 0))
                .build();

        Claim claim4 = Claim.builder()
                .createdDate(LocalDateTime.of(currentYear, 8, 5, 12, 0))
                .build();

        List<Claim> allClaims = Arrays.asList(claim1, claim2, claim3, claim4);

        when(claimRepository.findAllByOwner_Id(ownerId)).thenReturn(allClaims);

        List<Claim> result = claimService.findAllByOwnerIdLimit(ownerId);

        assertEquals(3, result.size());
        assertEquals(claim2.getCreatedDate(), result.get(0).getCreatedDate());
        assertEquals(claim1.getCreatedDate(), result.get(1).getCreatedDate());
        assertEquals(claim3.getCreatedDate(), result.get(2).getCreatedDate());
    }

}
