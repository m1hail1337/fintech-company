package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.DbContainer;
import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.grpc.dto.AgreementRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@Transactional
@DirtiesContext // gRPC requires
@SpringBootTest
public class AgreementCreationServiceIntegrationTest {

    @Container
    static DbContainer databaseContainer = DbContainer.getInstance();

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", databaseContainer::getUsername);
        registry.add("spring.datasource.password", databaseContainer::getPassword);
    }

    @Autowired
    AgreementCreationService service;

    @Autowired
    AgreementRepository agreementRepository;

    @Test
    void contextLoads() {
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
        List<Agreement> queryResult = agreementRepository.findByClientId(1L);
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
        List<Agreement> agreementsByQuery = agreementRepository.findByClientId(1L);
        assertEquals(2, agreementsByQuery.size());
        List<Long> agreementIDs = agreementsByQuery.stream().map(Agreement::getId).toList();
        assertTrue(agreementIDs.contains(firstAgreementId));
        assertTrue(agreementIDs.contains(secondAgreementId));
    }

    @Test
    void createSameAgreementsForTwoClient() {
        AgreementRecord firstAgreement = AgreementRecord.builder()
                .productCode("CL1.0")
                .clientId(1L)
                .term(12)
                .interest(new BigDecimal("0.07"))
                .disbursement(new BigDecimal("32000"))
                .build();
        Long firstClientAgreementId = service.createAgreement(firstAgreement);
        AgreementRecord secondAgreement = AgreementRecord.builder()
                .productCode("CL1.0")
                .clientId(2L)
                .term(12)
                .interest(new BigDecimal("0.07"))
                .disbursement(new BigDecimal("32000"))
                .build();
        Long secondClientAgreementId = service.createAgreement(secondAgreement);
        List<Agreement> agreementsForFirstClient = agreementRepository.findByClientId(1L);
        List<Agreement> agreementsForSecondClient = agreementRepository.findByClientId(2L);
        assertEquals(1, agreementsForFirstClient.size());
        assertEquals(1, agreementsForSecondClient.size());
        assertEquals(firstClientAgreementId, agreementsForFirstClient.get(0).getId());
        assertEquals(secondClientAgreementId, agreementsForSecondClient.get(0).getId());
        assertEquals(1, agreementsForFirstClient.get(0).getClientId());
        assertEquals(2, agreementsForSecondClient.get(0).getClientId());
    }
}
