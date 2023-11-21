package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
public class AgreementCreationServiceTest {

    @Autowired
    private AgreementCreationService service;

    @Autowired
    private AgreementRepository agreementRepository;

    @BeforeEach
    void cleanRepository() {
        agreementRepository.deleteAll();
    }

    @Test
    void checkDisbursementAndNextPaymentDatesWithoutDisbursement() {
        Long agreementId = service.createAgreement(
                1,
                12,
                BigDecimal.valueOf(32000),
                BigDecimal.valueOf(0.07),
                "CL1.0"
        );
        Agreement queryResult = agreementRepository.findById(agreementId).orElseThrow();
        assertNull(queryResult.getDisbursementDate());
        assertNull(queryResult.getNextPaymentDate());
    }

    @Test
    void createAgreementForOneClient() {
        Long expected = service.createAgreement(
                1,
                12,
                BigDecimal.valueOf(32000),
                BigDecimal.valueOf(0.07),
                "CL1.0"
        );
        List<Agreement> queryResult = agreementRepository.findByClientId(1);
        assertEquals(1, queryResult.size());
        assertEquals(expected, queryResult.get(0).getId());
    }

    @Test
    void createTwoAgreementsForOneClient() {
        Long firstAgreementId = service.createAgreement(
                1,
                12,
                BigDecimal.valueOf(32000),
                BigDecimal.valueOf(0.07),
                "CL1.0"
        );
        Long secondAgreementId = service.createAgreement(
                1,
                60,
                BigDecimal.valueOf(1000000),
                BigDecimal.valueOf(0.05),
                "CL1.0"
        );
        List<Agreement> agreementsByQuery = agreementRepository.findByClientId(1);
        assertEquals(2, agreementsByQuery.size());
        assertTrue(agreementsByQuery.stream().map(Agreement::getId).toList().contains(firstAgreementId));
        assertTrue(agreementsByQuery.stream().map(Agreement::getId).toList().contains(secondAgreementId));
    }

    @Test
    void createSameAgreementsForTwoClient() {
        Long firstClientAgreementId = service.createAgreement(
                1,
                12,
                BigDecimal.valueOf(32000),
                BigDecimal.valueOf(0.07),
                "CL1.0"
        );
        Long secondClientAgreementId = service.createAgreement(
                2,
                12,
                BigDecimal.valueOf(32000),
                BigDecimal.valueOf(0.07),
                "CL1.0"
        );
        List<Agreement> agreementsForFirstClient = agreementRepository.findByClientId(1);
        List<Agreement> agreementsForSecondClient = agreementRepository.findByClientId(2);
        assertEquals(1, agreementsForFirstClient.size());
        assertEquals(1, agreementsForSecondClient.size());
        assertEquals(firstClientAgreementId, agreementsForFirstClient.get(0).getId());
        assertEquals(secondClientAgreementId, agreementsForSecondClient.get(0).getId());
        assertEquals(1, agreementsForFirstClient.get(0).getClientId());
        assertEquals(2, agreementsForSecondClient.get(0).getClientId());
    }
}
