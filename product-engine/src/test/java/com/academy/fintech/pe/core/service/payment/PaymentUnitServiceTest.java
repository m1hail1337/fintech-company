package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.core.calculation.FinancialFunction;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitService;
import com.academy.fintech.pe.grpc.dto.AgreementDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PaymentUnitServiceTest {

    @Autowired
    PaymentUnitService service;

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
