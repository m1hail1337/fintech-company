package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.core.calculation.FinancialFunction;
import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentSchedule;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleRepository;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnit;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitRepository;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisbursementCreationService {

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private PaymentScheduleRepository scheduleRepository;

    @Autowired
    private PaymentUnitRepository unitRepository;

    public Long createSchedule(Long agreementNumber, LocalDate disbursementDate) {
        int newVersion = getLatestVersion(agreementNumber) + 1;
        PaymentSchedule schedule = new PaymentSchedule(agreementNumber,  newVersion);
        scheduleRepository.save(schedule);

        Agreement agreement = agreementRepository.findById(agreementNumber).orElseThrow();
        agreement.setDisbursementDate(disbursementDate);
        List<PaymentUnit> paymentUnits = createPaymentUnits(agreement, schedule);
        agreement.setNextPaymentDate(paymentUnits.get(0).getPaymentDate());
        unitRepository.saveAll(paymentUnits);
        agreementRepository.save(agreement);
        return schedule.getId();
    }

    private int getLatestVersion(Long agreementNumber) {
        int latest = 0;
        if (scheduleRepository.existsByAgreementNumber(agreementNumber)) {
            latest = scheduleRepository.findFirstByAgreementNumberOrderByVersionDesc(agreementNumber).getVersion();
        }
        return latest;
    }

    private List<PaymentUnit> createPaymentUnits(Agreement agreement, PaymentSchedule schedule) {
        List<PaymentUnit> paymentUnits = new ArrayList<>();
        int periods = agreement.getTerm();
        LocalDate currentDate = agreement.getDisbursementDate();
        BigDecimal principal = agreement.getPrincipalAmount();
        BigDecimal interest = agreement.getInterest();
        for (int currentPeriod = 1; currentPeriod <= periods; currentPeriod++) {
            currentDate = currentDate.plusMonths(1);
            PaymentUnit unit = new PaymentUnit(
                    schedule.getId(),
                    PaymentUnitStatus.FUTURE.name(),
                    currentDate.plusMonths(1),
                    FinancialFunction.pmt(interest, periods, principal),
                    FinancialFunction.ipmt(interest, currentPeriod, periods, principal),
                    FinancialFunction.ppmt(interest, currentPeriod, periods, principal),
                    currentPeriod
            );
            paymentUnits.add(unit);
        }
        return paymentUnits;
    }
}
