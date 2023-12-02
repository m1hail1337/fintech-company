package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.DbContainer;
import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.grpc.dto.AgreementDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
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
        String clientId = "checkDisbursementPaymentDates-client";
        Long agreementId = service.createAgreement(
                AgreementDto.builder()
                        .clientId(clientId)
                        .term(12)
                        .disbursement(BigDecimal.valueOf(32000))
                        .interest(BigDecimal.valueOf(0.07))
                        .productCode("CL1.0")
                        .build()
        );
        Agreement queryResult = agreementRepository.findById(agreementId).orElseThrow();
        assertNull(queryResult.getDisbursementDate());
        assertNull(queryResult.getNextPaymentDate());
    }

    @Test
    void createAgreementForOneClient() {
        String clientId = "createAgreementForClient-client";
        Long expected = service.createAgreement(
                AgreementDto.builder()
                        .clientId(clientId)
                        .term(12)
                        .disbursement(BigDecimal.valueOf(32000))
                        .interest(BigDecimal.valueOf(0.07))
                        .productCode("CL1.0")
                        .build()
        );
        List<Agreement> queryResult = agreementRepository.findByClientId(clientId);
        assertEquals(1, queryResult.size());
        assertEquals(expected, queryResult.get(0).getId());
    }

    @Test
    void createTwoAgreementsForOneClient() {
        String clientId = "twoAgreementsForOneClient-client";
        Long firstAgreementId = service.createAgreement(
                AgreementDto.builder()
                        .clientId(clientId)
                        .term(12)
                        .disbursement(BigDecimal.valueOf(32000))
                        .interest(BigDecimal.valueOf(0.07))
                        .productCode("CL1.0")
                        .build()
        );
        Long secondAgreementId = service.createAgreement(
                AgreementDto.builder()
                        .clientId(clientId)
                        .term(60)
                        .disbursement(BigDecimal.valueOf(1000000))
                        .interest(BigDecimal.valueOf(0.05))
                        .productCode("CL1.0")
                        .build()
        );
        List<Agreement> agreementsByQuery = agreementRepository.findByClientId(clientId);
        assertEquals(2, agreementsByQuery.size());
        List<Long> agreementIDs = agreementsByQuery.stream().map(Agreement::getId).toList();
        assertTrue(agreementIDs.contains(firstAgreementId));
        assertTrue(agreementIDs.contains(secondAgreementId));
    }

    @Test
    void createSameAgreementsForTwoClient() {
        String firstClientId = "createAgreementsTwoClients-client1";
        String secondClientId = "createAgreementsTwoClients-client2";
        AgreementDto firstAgreement = AgreementDto.builder()
                .productCode("CL1.0")
                .clientId(firstClientId)
                .term(12)
                .interest(new BigDecimal("0.07"))
                .disbursement(new BigDecimal("32000"))
                .build();
        Long firstClientAgreementId = service.createAgreement(firstAgreement);
        AgreementDto secondAgreement = AgreementDto.builder()
                .productCode("CL1.0")
                .clientId(secondClientId)
                .term(12)
                .interest(new BigDecimal("0.07"))
                .disbursement(new BigDecimal("32000"))
                .build();
        Long secondClientAgreementId = service.createAgreement(secondAgreement);
        List<Agreement> agreementsForFirstClient = agreementRepository.findByClientId(firstClientId);
        List<Agreement> agreementsForSecondClient = agreementRepository.findByClientId(secondClientId);
        assertEquals(1, agreementsForFirstClient.size());
        assertEquals(1, agreementsForSecondClient.size());
        assertEquals(firstClientAgreementId, agreementsForFirstClient.get(0).getId());
        assertEquals(secondClientAgreementId, agreementsForSecondClient.get(0).getId());
        assertEquals(firstClientId, agreementsForFirstClient.get(0).getClientId());
        assertEquals(secondClientId, agreementsForSecondClient.get(0).getClientId());
    }
}
