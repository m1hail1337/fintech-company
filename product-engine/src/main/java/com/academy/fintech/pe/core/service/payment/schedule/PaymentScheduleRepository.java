package com.academy.fintech.pe.core.service.payment.schedule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentScheduleRepository extends CrudRepository<PaymentSchedule, Long> {
    boolean existsByAgreementNumber(Long agreementNumber);
    PaymentSchedule findFirstByAgreementNumberOrderByVersionDesc(Long agreementNumber);
}
