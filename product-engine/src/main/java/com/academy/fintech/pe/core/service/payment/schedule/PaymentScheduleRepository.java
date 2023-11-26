package com.academy.fintech.pe.core.service.payment.schedule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentScheduleRepository extends CrudRepository<PaymentSchedule, Long> {
    Optional<PaymentSchedule> findFirstByAgreementNumberOrderByVersionDesc(Long agreementNumber);
}
