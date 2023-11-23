package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.core.service.agreement.db.AgreementDAO;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleDAO;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleRepository;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitDAO;
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
@DirtiesContext
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

    static AgreementDAO sampleAgreement = new AgreementDAO(
            "CL1.0",
            1,
            12,
            BigDecimal.valueOf(0.05),
            BigDecimal.valueOf(30000.00),
            BigDecimal.valueOf(2000.00)
    );

    @BeforeAll
    static void initSampleAgreementId() {
        sampleAgreement.setId(123L);
    }

    @PostConstruct
    void setUpMocksBehaviour() {
        when(scheduleRepository.existsByAgreementNumber(any())).thenReturn(false);
        when(agreementRepository.findById(any())).thenAnswer(invocation -> {
            long agreementId = invocation.getArgument(0);
            sampleAgreement.setId(agreementId);
            return Optional.of(sampleAgreement);
        });
        when(scheduleRepository.save(any())).thenAnswer(invocation -> {
            PaymentScheduleDAO schedule = invocation.getArgument(0);
            assertNull(schedule.getId());
            schedule.setId(++currentScheduleId);     //т.к. SERIAL
            return schedule;
        });
        when(unitRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(agreementRepository.save(any())).thenAnswer(invocation -> {
           AgreementDAO agreementDAO = invocation.getArgument(0);
           assertNotNull(agreementDAO.getDisbursementDate());
           assertNotNull(agreementDAO.getNextPaymentDate());
           return agreementDAO;
        });
    }

    @Test
    void checkVersionForAgreementWithExistingSchedule() {
        long existingScheduleId = 1L;
        currentScheduleId = existingScheduleId;
        long existingAgreementId = 123L;
        sampleAgreement.setId(123L);
        int version = 15;
        PaymentScheduleDAO oldPaymentSchedule = new PaymentScheduleDAO(
                existingScheduleId,
                existingAgreementId,
                version
        );
        PaymentScheduleDAO expectedPaymentSchedule = new PaymentScheduleDAO(
                existingScheduleId + 1,
                existingAgreementId,
                version + 1
        );
        when(scheduleRepository.existsByAgreementNumber(existingAgreementId)).thenReturn(true);
        when(scheduleRepository.findFirstByAgreementNumberOrderByVersionDesc(existingAgreementId)).thenReturn(oldPaymentSchedule);
        long newScheduleID = service.createSchedule(existingAgreementId, disbursementDate);
        verify(scheduleRepository).save(expectedPaymentSchedule);
        assertEquals(expectedPaymentSchedule.getId(), newScheduleID);
    }

    @Test
    void checkVersionForAgreementWithNotExistingSchedule() {
        currentScheduleId = 0;
        long existingAgreementId = 123L;
        PaymentScheduleDAO expectedPaymentSchedule = new PaymentScheduleDAO(1L, existingAgreementId, 1);
        long newScheduleID = service.createSchedule(existingAgreementId, disbursementDate);
        verify(scheduleRepository, never()).findFirstByAgreementNumberOrderByVersionDesc(any());
        verify(scheduleRepository).save(expectedPaymentSchedule);
        assertEquals(expectedPaymentSchedule.getId(), newScheduleID);
    }

    @Test
    void checkPaymentUnitCountWithDifferentTerm() {
        when(unitRepository.saveAll((anyList()))).thenAnswer(invocation -> {
            List<PaymentUnitDAO> units = invocation.getArgument(0);
            assertEquals(sampleAgreement.getTerm(), units.size());
            return units;
        });
        service.createSchedule(sampleAgreement.getId(), disbursementDate);
        sampleAgreement.setTerm(34);
        service.createSchedule(sampleAgreement.getId(), disbursementDate);
        sampleAgreement.setTerm(1);
    }

    @Test
    void createSchedule() {
        PaymentScheduleDAO expectedPaymentSchedule = new PaymentScheduleDAO(1L, sampleAgreement.getId(), 1);
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
        verify(scheduleRepository, times(1)).existsByAgreementNumber(notExistingAgreementId);
        verify(scheduleRepository, never()).save(any());
    }


}
