package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.core.calculation.FinancialFunction;
import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentSchedule;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnit;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitService;
import com.academy.fintech.pe.grpc.dto.AgreementDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext // gRPC requires
public class PaymentUnitServiceTest {

    @Autowired
    PaymentUnitService service;

    @Test
    void testCreatePaymentUnits() {
        long agreementNumber = 1L;
        int term = 12;
        Agreement agreement = Agreement.builder()
                .id(agreementNumber)
                .term(12)
                .interest(BigDecimal.valueOf(0.08))
                .principalAmount(BigDecimal.valueOf(30000))
                .disbursementDate(LocalDate.now())
                .build();
        long scheduleId = 123L;
        int scheduleVersion = 1;
        PaymentSchedule schedule = new PaymentSchedule(scheduleId, agreementNumber, scheduleVersion);
        List<PaymentUnit> actualPaymentUnits = service.createPaymentUnits(agreement, schedule);
        assertEquals(term, actualPaymentUnits.size());
        for (int i = 1; i <= term; i++) {
            assertEquals(i, actualPaymentUnits.get(i - 1).getPaymentPk().getPeriodNumber());
        }
        assertTrue(actualPaymentUnits.stream()
                .allMatch(paymentUnit -> paymentUnit.getPaymentPk().getScheduleId() == scheduleId));
        BigDecimal paymentPeriodPayment = actualPaymentUnits.get(0).getPeriodPayment();
        assertTrue(actualPaymentUnits.stream()
                .allMatch(paymentUnit -> paymentUnit.getPeriodPayment().compareTo(paymentPeriodPayment) == 0));
    }

    @Test
    void testGetMaxPayment() {
        BigDecimal disbursementAmount = BigDecimal.valueOf(30000);
        int term = 12;
        BigDecimal interest = BigDecimal.valueOf(0.05);
        AgreementDto agreementDto = AgreementDto.builder()
                .term(term)
                .productCode("CL1.0")
                .disbursement(disbursementAmount)
                .interest(interest)
                .build();
        BigDecimal expected = FinancialFunction.pmt(interest, term, disbursementAmount);
        BigDecimal actual = service.getMaxPayment(agreementDto);
        assertEquals(expected, actual);
    }
}
