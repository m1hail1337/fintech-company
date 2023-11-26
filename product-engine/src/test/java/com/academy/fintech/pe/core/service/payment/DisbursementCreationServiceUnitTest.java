package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentSchedule;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleRepository;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnit;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext // gRPC requires
public class DisbursementCreationServiceUnitTest {

    @MockBean
    static AgreementRepository agreementRepository;

    @MockBean
    static PaymentScheduleRepository scheduleRepository;

    @MockBean
    static PaymentUnitRepository unitRepository;

    @Autowired
    DisbursementCreationService service;

    static long currentScheduleId = 0;
    LocalDate disbursementDate = LocalDate.now();

    static Agreement sampleAgreement = new Agreement(
            "CL1.0",
            1L,
            BigDecimal.valueOf(0.05),
            12,
            BigDecimal.valueOf(30000.00),
            BigDecimal.valueOf(2000.00)
    );

    @BeforeAll
    static void initSampleAgreementId() {
        sampleAgreement.setId(123L);
    }

    @PostConstruct
    void setUpMocksBehaviour() {
        when(agreementRepository.findById(any())).thenAnswer(invocation -> {
            long agreementId = invocation.getArgument(0);
            sampleAgreement.setId(agreementId);
            return Optional.of(sampleAgreement);
        });
        when(scheduleRepository.save(any())).thenAnswer(invocation -> {
            PaymentSchedule schedule = invocation.getArgument(0);
            assertNull(schedule.getId());
            schedule.setId(++currentScheduleId);     //т.к. SERIAL
            return schedule;
        });
        when(unitRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(agreementRepository.save(any())).thenAnswer(invocation -> {
           Agreement agreement = invocation.getArgument(0);
           assertNotNull(agreement.getDisbursementDate());
           assertNotNull(agreement.getNextPaymentDate());
           return agreement;
        });
    }

    @Test
    void checkVersionForAgreementWithExistingSchedule() {
        long existingScheduleId = 1L;
        currentScheduleId = existingScheduleId;
        long existingAgreementId = 123L;
        sampleAgreement.setId(123L);
        int version = 15;
        PaymentSchedule oldPaymentSchedule = new PaymentSchedule(
                existingScheduleId,
                existingAgreementId,
                version
        );
        PaymentSchedule expectedPaymentSchedule = new PaymentSchedule(
                existingScheduleId + 1,
                existingAgreementId,
                version + 1
        );
        when(scheduleRepository.findFirstByAgreementNumberOrderByVersionDesc(existingAgreementId)).thenReturn(Optional.of(oldPaymentSchedule));
        long newScheduleID = service.createSchedule(existingAgreementId, disbursementDate);
        verify(scheduleRepository).save(expectedPaymentSchedule);
        assertEquals(expectedPaymentSchedule.getId(), newScheduleID);
    }

    @Test
    void checkVersionForAgreementWithNotExistingSchedule() {
        currentScheduleId = 0;
        long existingAgreementId = 123L;
        PaymentSchedule expectedPaymentSchedule = new PaymentSchedule(1L, existingAgreementId, 1);
        long newScheduleID = service.createSchedule(existingAgreementId, disbursementDate);
        verify(scheduleRepository, times(1)).findFirstByAgreementNumberOrderByVersionDesc(existingAgreementId);
        verify(scheduleRepository).save(expectedPaymentSchedule);
        assertEquals(expectedPaymentSchedule.getId(), newScheduleID);
    }

    @Test
    void checkPaymentUnitCountWithTermOf12() {
        when(unitRepository.saveAll((anyList()))).thenAnswer(invocation -> {
            List<PaymentUnit> units = invocation.getArgument(0);
            assertEquals(sampleAgreement.getTerm(), units.size());
            return units;
        });
        service.createSchedule(sampleAgreement.getId(), disbursementDate);
        Agreement agreementWith1Term = new Agreement(
                "CL1.0",
                1L,
                BigDecimal.valueOf(0.05),
                1,
                BigDecimal.valueOf(1000000.00),
                BigDecimal.valueOf(10000.00)
        );
        agreementWith1Term.setId(2L);
        service.createSchedule(agreementWith1Term.getId(), disbursementDate);
    }


    @Test
    void checkPaymentUnitCountWithTermOf34() {
        Agreement agreementWith34Term = new Agreement(
                "CL1.0",
                1L,
                BigDecimal.valueOf(0.05),
                34,
                BigDecimal.valueOf(1000000.00),
                BigDecimal.valueOf(10000.00)
        );
        agreementWith34Term.setId(1L);
        service.createSchedule(agreementWith34Term.getId(), disbursementDate);
        Agreement agreementWith1Term = new Agreement(
                "CL1.0",
                1L,
                BigDecimal.valueOf(0.05),
                1,
                BigDecimal.valueOf(1000000.00),
                BigDecimal.valueOf(10000.00)
        );
        agreementWith1Term.setId(2L);
        service.createSchedule(agreementWith1Term.getId(), disbursementDate);
    }

    @Test
    void checkPaymentUnitCountWithTermOf1() {
        Agreement agreementWith1Term = new Agreement(
                "CL1.0",
                1L,
                BigDecimal.valueOf(0.05),
                1,
                BigDecimal.valueOf(1000000.00),
                BigDecimal.valueOf(10000.00)
        );
        agreementWith1Term.setId(2L);
        service.createSchedule(agreementWith1Term.getId(), disbursementDate);
    }

    @Test
    void createSchedule() {
        PaymentSchedule expectedPaymentSchedule = new PaymentSchedule(1L, sampleAgreement.getId(), 1);
        currentScheduleId = 0L;
        Long scheduleId = service.createSchedule(sampleAgreement.getId(), disbursementDate);
        sampleAgreement.setDisbursementDate(disbursementDate);
        sampleAgreement.setNextPaymentDate(disbursementDate.plusMonths(1));
        verify(scheduleRepository).save(expectedPaymentSchedule);
        verify(agreementRepository).save(sampleAgreement);
        verify(agreementRepository, times(1)).save(any());
        verify(scheduleRepository, times(1)).save(any());
        assertEquals(expectedPaymentSchedule.getId(), scheduleId);
    }

    @Test
    void createScheduleWithNotExistingAgreement() {
        long notExistingAgreementId = 999L;
        when(agreementRepository.findById(notExistingAgreementId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> {
            service.createSchedule(sampleAgreement.getId(), disbursementDate);
        });
        verify(scheduleRepository, never()).save(any());
    }


}
