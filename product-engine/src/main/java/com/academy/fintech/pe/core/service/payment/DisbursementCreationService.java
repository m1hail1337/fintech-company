package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.core.calculation.FinancialFunction;
import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentSchedule;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleRepository;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentPk;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnit;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisbursementCreationService {

    private final AgreementRepository agreementRepository;

    private final PaymentScheduleRepository scheduleRepository;

    private final PaymentUnitRepository unitRepository;

    public Long createSchedule(Long agreementNumber, LocalDate disbursementDate) {
        int newVersion = getLatestVersion(agreementNumber) + 1;
        PaymentSchedule schedule = new PaymentSchedule(agreementNumber,  newVersion);
        Agreement agreement = agreementRepository.findById(agreementNumber).orElseThrow();
        scheduleRepository.save(schedule);

        agreement.setDisbursementDate(disbursementDate);
        List<PaymentUnit> paymentUnits = createPaymentUnits(agreement, schedule);
        PaymentUnit firstPayment = paymentUnits.get(0);
        agreement.setNextPaymentDate(firstPayment.getPaymentDate());
        unitRepository.saveAll(paymentUnits);
        agreementRepository.save(agreement);
        return schedule.getId();
    }

    private int getLatestVersion(Long agreementNumber) {
        return scheduleRepository
                .findFirstByAgreementNumberOrderByVersionDesc(agreementNumber)
                .map(PaymentSchedule::getVersion)
                .orElse(0);
    }

    private List<PaymentUnit> createPaymentUnits(Agreement agreement, PaymentSchedule schedule) {
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
}
