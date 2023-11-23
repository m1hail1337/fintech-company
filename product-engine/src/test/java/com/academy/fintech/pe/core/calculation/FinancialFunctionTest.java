package com.academy.fintech.pe.core.calculation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.academy.fintech.pe.core.calculation.FinancialFunction.pmt;
import static com.academy.fintech.pe.core.calculation.FinancialFunction.ipmt;
import static com.academy.fintech.pe.core.calculation.FinancialFunction.ppmt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FinancialFunctionTest {

    @Test
    void pmtTest() {
        assertEquals(BigDecimal.valueOf(-89.17),
                pmt(BigDecimal.valueOf(0.07), 12, BigDecimal.valueOf(1000)));
        assertEquals(BigDecimal.valueOf(-891.67),
                pmt(BigDecimal.valueOf(0.07), 12, BigDecimal.valueOf(10000)));
        assertEquals(BigDecimal.valueOf(-592.76),
                pmt(BigDecimal.valueOf(0.1), 24, BigDecimal.valueOf(12345)));
        assertEquals(BigDecimal.valueOf(-2309.75),
                pmt(BigDecimal.valueOf(0.05), 60, BigDecimal.valueOf(120000)));
    }

    @Test
    void ipmtTest() {
        assertEquals(BigDecimal.valueOf(-214.74),
                ipmt(BigDecimal.valueOf(0.05), 48, 60, BigDecimal.valueOf(120000)));
        assertEquals(BigDecimal.valueOf(-102.88),
                ipmt(BigDecimal.valueOf(0.1), 12, 24, BigDecimal.valueOf(12345)));
        assertEquals(BigDecimal.valueOf(-500).setScale(2, RoundingMode.HALF_UP),
                ipmt(BigDecimal.valueOf(0.05), 12, 60, BigDecimal.valueOf(120000)));
    }

    @Test
    void ppmtTest() {
        assertEquals(BigDecimal.valueOf(-2095.01),
                ppmt(BigDecimal.valueOf(0.05), 48, 60, BigDecimal.valueOf(120000)));
        assertEquals(BigDecimal.valueOf(-489.88),
                ppmt(BigDecimal.valueOf(0.1), 12, 24, BigDecimal.valueOf(12345)));
    }
}
