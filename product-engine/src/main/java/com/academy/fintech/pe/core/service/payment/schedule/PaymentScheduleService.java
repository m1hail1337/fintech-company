package com.academy.fintech.pe.core.service.payment.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentScheduleService {

    private final PaymentScheduleRepository repository;

    public PaymentSchedule createSchedule(Long agreementId) {
        int newVersion = getLatestVersion(agreementId) + 1;
        return new PaymentSchedule(agreementId,  newVersion);
    }

    public PaymentSchedule saveSchedule(PaymentSchedule schedule) {
        return repository.save(schedule);
    }

    private int getLatestVersion(Long agreementNumber) {
        return repository
                .findFirstByAgreementNumberOrderByVersionDesc(agreementNumber)
                .map(PaymentSchedule::getVersion)
                .orElse(0);
    }
}
