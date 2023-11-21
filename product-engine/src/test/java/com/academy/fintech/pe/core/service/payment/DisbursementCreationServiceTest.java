package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.core.service.agreement.AgreementCreationService;
import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentSchedule;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleRepository;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnit;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
public class DisbursementCreationServiceTest {

    @Autowired
    private DisbursementCreationService disbursementService;

    @Autowired
    private AgreementCreationService agreementService;

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private PaymentScheduleRepository scheduleRepository;

    @Autowired
    private PaymentUnitRepository unitRepository;

    @BeforeEach
    void cleanRepository() {
        agreementRepository.deleteAll();
        scheduleRepository.deleteAll();
        unitRepository.deleteAll();
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
        PaymentSchedule scheduleQuery = scheduleRepository.findById(scheduleId).orElseThrow();
        assertEquals(1, scheduleQuery.getVersion());
        assertEquals(agreementId, scheduleQuery.getAgreementNumber());
        List<PaymentUnit> paymentUnits = unitRepository.findAllByScheduleId(scheduleId);
        assertEquals(12, paymentUnits.size());
        String firstPaymentDate = paymentUnits.stream().filter(unit -> unit.getPeriodNumber() == 1)
                .findAny().orElseThrow().getPaymentDate().toString();
        assertEquals(disbursementDate.plusMonths(1), LocalDate.parse(firstPaymentDate));
        Agreement updatedAgreement = agreementRepository.findById(agreementId).orElseThrow();
        assertEquals(disbursementDate, LocalDate.parse(updatedAgreement.getDisbursementDate().toString()));
        assertEquals(disbursementDate.plusMonths(1), LocalDate.parse(
                updatedAgreement.getNextPaymentDate().toString()));
    }
}
