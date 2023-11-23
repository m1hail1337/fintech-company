package com.academy.fintech.pe.core.service.payment.schedule.unit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentUnitRepository extends CrudRepository<PaymentUnitDAO, Long> {
    List<PaymentUnitDAO> findAllByScheduleId(Long SchedulePaymentId);
}
