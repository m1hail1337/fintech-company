package com.academy.fintech.pe.core.service.payment;

import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementService;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentSchedule;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleService;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnit;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisbursementCreationService {

    private final AgreementService agreementService;

    private final PaymentScheduleService scheduleService;

    private final PaymentUnitService paymentUnitService;

    @Transactional
    public Long createDisbursement(Long agreementId, LocalDate disbursementDate) {
        Agreement agreement = agreementService.findById(agreementId).orElseThrow();
        PaymentSchedule schedule = scheduleService.createSchedule(agreementId);
        schedule = scheduleService.saveSchedule(schedule);  //  Чтобы получить сущность с Id
        agreement.setDisbursementDate(disbursementDate);
        List<PaymentUnit> paymentUnits = paymentUnitService.createPaymentUnits(agreement, schedule);
        PaymentUnit firstPayment = paymentUnits.get(0);
        agreement.setNextPaymentDate(firstPayment.getPaymentDate());
        paymentUnitService.saveAllUnits(paymentUnits);
        agreementService.saveAgreement(agreement);
        return schedule.getId();
    }
}
