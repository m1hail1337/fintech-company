package com.academy.fintech.pe.core.service.payment.schedule.unit;

import com.academy.fintech.pe.core.calculation.FinancialFunction;
import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementService;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentSchedule;
import com.academy.fintech.pe.core.service.payment.schedule.PaymentScheduleService;
import com.academy.fintech.pe.grpc.dto.AgreementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentUnitService {

    private final PaymentUnitRepository repository;

    private final PaymentScheduleService scheduleService;

    private final AgreementService agreementService;

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

    public BigDecimal getMaxPayment(AgreementDto agreementDto) {
        Agreement tempAgreement = buildTempAgreement(agreementDto);
        PaymentSchedule tempSchedule = new PaymentSchedule(0L, 0);
        List<PaymentUnit> paymentUnits = createPaymentUnits(tempAgreement, tempSchedule);
        List<BigDecimal> payments = paymentUnits.stream().map(PaymentUnit::getPeriodPayment).toList();
        return payments.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    public List<Integer> getLoansOverdueDays(String clientId) {
        List<Long> agreementIds = agreementService.findAllAgreementIdByClientId(clientId);
        List<Long> scheduleIds = getScheduleIds(agreementIds);
        List<PaymentUnit> allClientPayments = getAllPaymentsFromSchedules(scheduleIds);
        List<PaymentUnit> overduePayments = findOverduePaymentUnits(allClientPayments);
        return overduePayments.stream().mapToInt(payment ->
                (int) ChronoUnit.DAYS.between(LocalDate.now(), payment.getPaymentDate())
        ).boxed().collect(Collectors.toList());
    }

    private List<PaymentUnit> getAllPaymentsFromSchedules(List<Long> scheduleIds) {
        List<PaymentUnit> payments = new ArrayList<>();
        for (Long scheduleId : scheduleIds) {
            payments.addAll(repository.findAllByPaymentPk_ScheduleId(scheduleId));
        }
        return payments;
    }

    private List<Long> getScheduleIds(List<Long> agreementIds) {
        List<Long> scheduleIds = new ArrayList<>();
        for (long agreementId : agreementIds) {
            scheduleIds.addAll(scheduleService.findAllScheduleIdByAgreementId(agreementId));
        }
        return scheduleIds;
    }

    public void saveAllUnits(Iterable<PaymentUnit> units) {
        repository.saveAll(units);
    }

    private List<PaymentUnit> findOverduePaymentUnits(List<PaymentUnit> paymentUnits) {
        return paymentUnits.stream().filter(payment -> payment.getStatus() == PaymentUnitStatus.OVERDUE).toList();
    }

    private Agreement buildTempAgreement(AgreementDto agreementDto) {
        return Agreement.builder()
                .term(agreementDto.term())
                .interest(agreementDto.interest())
                .principalAmount(agreementDto.disbursement())
                .disbursementDate(LocalDate.now())
                .build();
    }
}
