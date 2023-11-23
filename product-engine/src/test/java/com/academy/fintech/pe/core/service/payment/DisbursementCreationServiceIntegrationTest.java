package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.DBContainer;
import com.academy.fintech.pe.core.service.agreement.AgreementCreationService;
import com.academy.fintech.pe.core.service.agreement.db.AgreementDAO;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleDAO;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleRepository;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitDAO;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitRepository;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@Testcontainers
@DirtiesContext
@SpringBootTest
public class DisbursementCreationServiceIntegrationTest {

    @Container
    static DBContainer databaseContainer = DBContainer.getInstance();

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", databaseContainer::getUsername);
        registry.add("spring.datasource.password", databaseContainer::getPassword);
    }

    @Autowired
    DisbursementCreationService disbursementService;

    @Autowired
    AgreementCreationService agreementService;

    @Autowired
    AgreementRepository agreementRepository;

    @Autowired
    PaymentScheduleRepository scheduleRepository;

    @Autowired
    PaymentUnitRepository unitRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void createDisbursement() {
        LocalDate disbursementDate = LocalDate.of(2023, 11, 21);
        Long agreementId = agreementService.createAgreement(
                1,
                12,
                BigDecimal.valueOf(32000),
                BigDecimal.valueOf(0.07),
                "CL1.0"
        );
        Long scheduleId = disbursementService.createSchedule(agreementId, disbursementDate);
        assertTrue(scheduleRepository.existsByAgreementNumber(agreementId));
        PaymentScheduleDAO scheduleQuery = scheduleRepository.findById(scheduleId).orElseThrow();
        assertEquals(1, scheduleQuery.getVersion());
        assertEquals(agreementId, scheduleQuery.getAgreementNumber());
        List<PaymentUnitDAO> paymentUnits = unitRepository.findAllByScheduleId(scheduleId);
        assertEquals(12, paymentUnits.size());
        String firstPaymentDate = paymentUnits.stream().filter(unit -> unit.getPeriodNumber() == 1)
                .findAny().orElseThrow().getPaymentDate().toString();
        assertEquals(disbursementDate.plusMonths(1), LocalDate.parse(firstPaymentDate));
        AgreementDAO updatedAgreement = agreementRepository.findById(agreementId).orElseThrow();
        assertEquals(disbursementDate, LocalDate.parse(updatedAgreement.getDisbursementDate().toString()));
        assertEquals(disbursementDate.plusMonths(1), LocalDate.parse(
                updatedAgreement.getNextPaymentDate().toString()));
    }
}