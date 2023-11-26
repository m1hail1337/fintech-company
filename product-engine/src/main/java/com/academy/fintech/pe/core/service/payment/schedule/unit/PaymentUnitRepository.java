package com.academy.fintech.pe.core.service.payment.schedule.unit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentUnitRepository extends CrudRepository<PaymentUnit, PaymentPk> {

    List<PaymentUnit> findAllByPaymentPk_ScheduleId(Long scheduleId);
}
