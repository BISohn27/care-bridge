package com.donation.carebridge.donation.domain.donation.application.in;

import com.donation.carebridge.donation.domain.donation.application.DonationService;
import com.donation.carebridge.donation.domain.donation.application.out.DonationRepository;
import com.donation.carebridge.donation.domain.donation.dto.DonationRegisterResult;
import com.donation.carebridge.donation.domain.donation.dto.DonationRegisterCommand;
import com.donation.carebridge.donation.domain.donation.model.Donation;
import com.donation.carebridge.donation.domain.donation.model.DonationStatus;
import com.donation.carebridge.donation.domain.donationcase.application.in.DonationCaseFinder;
import com.donation.carebridge.donation.domain.donationcase.application.model.DonationCase;
import com.donation.carebridge.donation.domain.payment.application.in.PaymentInitiator;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentResult;
import com.donation.carebridge.donation.domain.payment.dto.PaymentMethod;
import com.donation.carebridge.donation.domain.payment.model.Currency;
import com.donation.carebridge.donation.domain.payment.model.PaymentStatus;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationRegisterTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DonationCaseFinder donationCaseFinder;

    @Mock
    private PaymentInitiator paymentInitiator;

    @InjectMocks
    private DonationService donationRegister;

    private static DonationCase fundableCase;
    private static DonationCase notFundableCase;

    @BeforeAll
    static void initialize() {
        fundableCase = mock(DonationCase.class);
        when(fundableCase.isFundable()).thenReturn(true);

        notFundableCase = mock(DonationCase.class);
        when(notFundableCase.isFundable()).thenReturn(false);
    }

    private DonationRegisterCommand validRequest() {
        return new DonationRegisterCommand(
                "case-1",
                "donor-1",
                Currency.KRW,
                10000L,
                PgProviderCode.TOSS,
                PaymentMethod.CARD,
                "idem-123"
        );
    }

    private CreatePaymentResult paymentResult() {
        return new CreatePaymentResult(
                "payment-1",
                PaymentStatus.CREATED,
                null
        );
    }

    @Test
    @DisplayName("유효한 요청으로 후원 등록 시 PENDING 상태의 Donation이 생성되고 Payment가 시작된다")
    void should_createDonationAndInitiatePayment_when_validRequest() {
        DonationRegisterCommand request = validRequest();
        Donation donation = Donation.create(fundableCase, request.donorId(), request.amount(), request.currency());
        CreatePaymentResult paymentResult = paymentResult();

        when(donationCaseFinder.find(request.donationCaseId())).thenReturn(fundableCase);
        when(donationRepository.find(request.donationCaseId(), request.donorId(), DonationStatus.PENDING))
                .thenReturn(Optional.empty());
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);
        when(paymentInitiator.initiate(any(CreatePaymentRequest.class))).thenReturn(paymentResult);

        DonationRegisterResult result = donationRegister.register(request);

        assertThat(result).isNotNull();
        assertThat(result.currency()).isEqualTo(Currency.KRW);
        assertThat(result.amount()).isEqualTo(10000L);
        assertThat(result.payment()).isEqualTo(paymentResult);

        checkDonationSave(request);
        checkPaymentInitiate(request);
    }

    private void checkDonationSave(DonationRegisterCommand request) {
        ArgumentCaptor<Donation> repositoryCaptor = ArgumentCaptor.forClass(Donation.class);
        verify(donationRepository).save(repositoryCaptor.capture());

        Donation savedDonation = repositoryCaptor.getValue();
        assertThat(savedDonation.getStatus()).isEqualTo(DonationStatus.PENDING);
        assertThat(savedDonation.getAmount()).isEqualTo(request.amount());
        assertThat(savedDonation.getCurrency()).isEqualTo(request.currency());
    }

    private void checkPaymentInitiate(DonationRegisterCommand request) {
        ArgumentCaptor<CreatePaymentRequest> paymentCaptor = ArgumentCaptor.forClass(CreatePaymentRequest.class);
        verify(paymentInitiator).initiate(paymentCaptor.capture());

        CreatePaymentRequest paymentRequest = paymentCaptor.getValue();
        assertThat(paymentRequest.amount()).isEqualTo(request.amount());
        assertThat(paymentRequest.currency()).isEqualTo(request.currency());
        assertThat(paymentRequest.pgProviderCode()).isEqualTo(request.pgProviderCode());
        assertThat(paymentRequest.paymentMethod()).isEqualTo(request.paymentMethod());
        assertThat(paymentRequest.idempotencyKey()).isEqualTo(request.idempotencyKey());
    }

    @Test
    @DisplayName("Case가 FUNDING 상태가 아니면 후원 등록이 실패한다")
    void should_throwException_when_caseIsNotFundable() {
        DonationRegisterCommand request = validRequest();

        when(donationCaseFinder.find(request.donationCaseId())).thenReturn(notFundableCase);

        assertThatThrownBy(() -> donationRegister.register(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not fundable");

        verify(donationRepository, never()).save(any(Donation.class));
        verify(paymentInitiator, never()).initiate(any(CreatePaymentRequest.class));
    }

    @Test
    @DisplayName("동일한 Case와 Donor에 대해 PENDING 상태의 Donation이 이미 존재하면 새 후원 등록이 실패한다")
    void should_throwException_when_pendingDonationAlreadyExists() {
        DonationRegisterCommand request = validRequest();
        Donation existingDonation = Donation.create(fundableCase, request.donorId(), request.amount(), request.currency());

        when(donationCaseFinder.find(request.donationCaseId())).thenReturn(fundableCase);
        when(donationRepository.find(request.donationCaseId(), request.donorId(), DonationStatus.PENDING))
                .thenReturn(Optional.of(existingDonation));

        // when & then
        assertThatThrownBy(() -> donationRegister.register(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");

        verify(donationRepository, never()).save(any(Donation.class));
        verify(paymentInitiator, never()).initiate(any(CreatePaymentRequest.class));
    }

    @Test
    @DisplayName("후원 금액이 최소 금액보다 작으면 Donation 생성이 실패한다")
    void should_throwException_when_amountIsBelowMinimum() {
        assertThatThrownBy(() -> new DonationRegisterCommand(
                "case-1",
                "donor-1",
                Currency.KRW,
                500L,
                PgProviderCode.TOSS,
                PaymentMethod.CARD,
                "idem-123"
        ))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 Case에 후원하려 하면 실패한다")
    void should_throwException_when_caseNotFound() {
        DonationRegisterCommand request = validRequest();

        when(donationCaseFinder.find(request.donationCaseId()))
                .thenThrow(new IllegalArgumentException("Case not found"));

        assertThatThrownBy(() -> donationRegister.register(request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(donationRepository, never()).save(any(Donation.class));
        verify(paymentInitiator, never()).initiate(any(CreatePaymentRequest.class));
    }

    @Test
    @DisplayName("Payment 시작 실패 시 예외가 전파된다")
    void should_propagateException_when_paymentInitiationFails() {
        DonationRegisterCommand request = validRequest();
        Donation donation = Donation.create(fundableCase, request.donorId(), request.amount(), request.currency());

        when(donationCaseFinder.find(request.donationCaseId())).thenReturn(fundableCase);
        when(donationRepository.find(request.donationCaseId(), request.donorId(), DonationStatus.PENDING))
                .thenReturn(Optional.empty());
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);
        when(paymentInitiator.initiate(any(CreatePaymentRequest.class)))
                .thenThrow(new RuntimeException("Payment initiation failed"));

        assertThatThrownBy(() -> donationRegister.register(request))
                .isInstanceOf(RuntimeException.class);
    }
}
