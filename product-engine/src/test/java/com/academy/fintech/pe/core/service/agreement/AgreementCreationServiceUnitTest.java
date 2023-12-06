package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.product.db.Product;
import com.academy.fintech.pe.core.service.product.db.ProductRepository;
import com.academy.fintech.pe.grpc.dto.AgreementDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.annotation.PostConstruct;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext // gRPC requires
public class AgreementCreationServiceUnitTest {

    @MockBean
    static AgreementRepository agreementRepository;

    @MockBean
    static ProductRepository productRepository;

    @Autowired
    AgreementCreationService service;

    static Product cashLoan1_0 = new Product(
            "CL1.0",
            3,
            18,
            BigDecimal.valueOf(50000.00),
            BigDecimal.valueOf(500000.00),
            BigDecimal.valueOf(0.08),
            BigDecimal.valueOf(0.15),
            BigDecimal.valueOf(2000.00),
            BigDecimal.valueOf(10000.00)
    );

    static Long currentAgreementId = 1L;

    @PostConstruct
    static void setUpMocksBehaviour() {
        when(productRepository.findById(cashLoan1_0.getCode())).thenReturn(Optional.of(cashLoan1_0));
        when(agreementRepository.save(any())).thenAnswer(invocation -> {
            Agreement savedAgreement = invocation.getArgument(0);
            assertNull(savedAgreement.getId());
            savedAgreement.setId(currentAgreementId);   // т.к id в бд SERIAL
            currentAgreementId++;
            return savedAgreement;
        });
    }

    @Test
    void createAgreement() {
        Agreement agreement = Agreement.builder()
                .productCode(cashLoan1_0.getCode())
                .clientId("1")
                .interest(BigDecimal.valueOf(0.05))
                .term(12)
                .principalAmount(BigDecimal.valueOf(30000.00))
                .originationAmount(cashLoan1_0.getMinOriginationAmount())
                .build();
        Long actualID = service.createAgreement( AgreementDto.builder()
                        .clientId(agreement.getClientId())
                        .term(agreement.getTerm())
                        .disbursement(agreement.getPrincipalAmount().subtract(agreement.getOriginationAmount()))
                        .interest(agreement.getInterest())
                        .productCode(agreement.getProductCode())
                        .build()
        );
        verify(productRepository).findById(cashLoan1_0.getCode());
        verify(agreementRepository, times(1)).save(any());
        assertEquals(1L, actualID);
    }

    @Test
    void createAgreementWithWrongProduct() {
        String wrongProductCode = "SomeWrongProductCode";
        assertThrows(NoSuchElementException.class, () -> service.createAgreement(
                AgreementDto.builder()
                        .clientId("1")
                        .term(12)
                        .disbursement(BigDecimal.valueOf(30000.00))
                        .interest(BigDecimal.valueOf(0.05))
                        .productCode(wrongProductCode)
                        .build()
        ));
        verify(productRepository).findById(wrongProductCode);
        verify(agreementRepository, never()).save(any());
    }
}
