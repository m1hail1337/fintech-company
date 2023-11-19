package com.academy.fintech.pe.core.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FinancialFunction {

    private static final int SCALE = 2;
    private static final double MONTH_PER_YEAR = 12.0;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;


    /**
     * @return величина выплаты в месяц для кредита
     */
    public static BigDecimal pmt(BigDecimal interest, int monthTerm, BigDecimal disbursement) {
        BigDecimal interestPlusOnePowYears = BigDecimal.valueOf(
                Math.pow(interest.doubleValue() + 1, monthTerm / MONTH_PER_YEAR)
        );
        BigDecimal numerator = interest.multiply(disbursement.multiply(interestPlusOnePowYears));
        BigDecimal denominator = interestPlusOnePowYears.subtract(BigDecimal.ONE);
        BigDecimal paymentPerYear = numerator.divide(denominator, SCALE, ROUNDING_MODE);

        return paymentPerYear.divide(BigDecimal.valueOf(MONTH_PER_YEAR), SCALE, ROUNDING_MODE).negate();
    }


    /**
     * @return величина платежа за проценты за конкретный месяц
     */
    public static BigDecimal ipmt(BigDecimal interest, int currentMonthTerm, int totalMonthTerm, BigDecimal disbursement) {
        BigDecimal interestPlusOne = interest.add(BigDecimal.ONE);
        double totalYears = totalMonthTerm / MONTH_PER_YEAR;
        double currentYears = currentMonthTerm / MONTH_PER_YEAR;
        BigDecimal interestPlusOnePowTotalYears = BigDecimal.valueOf(
                Math.pow(interestPlusOne.doubleValue(), totalYears)
        );
        BigDecimal interestPlusOnePowCurrentYears = BigDecimal.valueOf(
                Math.pow(interestPlusOne.doubleValue(), currentYears)
        );
        BigDecimal numerator = disbursement.multiply(interest)
                .multiply(interestPlusOnePowTotalYears.multiply(interestPlusOne).subtract(interestPlusOnePowCurrentYears));
        BigDecimal denominator = interestPlusOne.multiply(interestPlusOnePowTotalYears.subtract(BigDecimal.ONE));
        BigDecimal interestPaymentInCurrentYear = numerator.divide(denominator, SCALE, ROUNDING_MODE);

        return interestPaymentInCurrentYear.divide(BigDecimal.valueOf(MONTH_PER_YEAR), SCALE, ROUNDING_MODE).negate();
    }

    /**
     * @return величина платежа в основную сумму в конкретный месяц
     */
    public static BigDecimal ppmt(BigDecimal interest, int currentMonthTerm, int totalMonthTerm, BigDecimal disbursement) {
        BigDecimal pmt = pmt(interest, totalMonthTerm, disbursement);
        BigDecimal ipmt = ipmt(interest, currentMonthTerm, totalMonthTerm, disbursement);
        return pmt.subtract(ipmt);
    }
}
