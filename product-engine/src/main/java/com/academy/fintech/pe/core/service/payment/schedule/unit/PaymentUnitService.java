package com.academy.fintech.pe.core.service.payment.schedule.unit;

import com.academy.fintech.pe.core.calculation.FinancialFunction;
import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentUnitService {

    private final PaymentUnitRepository repository;

    public List<PaymentUnit> createPaymentUnits(Agreement agreement, PaymentSchedule schedule) {
        List<PaymentUnit> paymentUnits = new ArrayList<>();
        int periods = agreement.getTerm();
        LocalDate currentDate = agreement.getDisbursementDate();
        BigDecimal principal = agreement.getPrincipalAmount();
        BigDecimal interest = agreement.getInterest();
        for (int currentPeriod = 1; currentPeriod <= periods; currentPeriod++) {
            PaymentPk paymentPk = new PaymentPk(schedule.getId(), currentPeriod);
            PaymentUnit unit = new PaymentUnit(
                    paymentPk,
                    currentDate.plusMonths(1),
                    FinancialFunction.pmt(interest, periods, principal),
                    FinancialFunction.ipmt(interest, currentPeriod, periods, principal),
                    FinancialFunction.ppmt(interest, currentPeriod, periods, principal)
            );
            currentDate = currentDate.plusMonths(1);
            paymentUnits.add(unit);
        }
        return paymentUnits;
    }

    public void saveAllUnits(Iterable<PaymentUnit> units) {
        repository.saveAll(units);
    }
}
