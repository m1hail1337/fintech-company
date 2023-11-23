package com.academy.fintech.pe.core.service.payment.schedule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentScheduleRepository extends CrudRepository<PaymentScheduleDAO, Long> {
    boolean existsByAgreementNumber(Long agreementNumber);
    PaymentScheduleDAO findFirstByAgreementNumberOrderByVersionDesc(Long agreementNumber);
}
