package com.academy.fintech.pe.core.service.payment.schedule.unit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class PaymentPk implements Serializable {
    @Column(name = "payment_schedule_id")
    private Long scheduleId;
    @Column(name = "period_number")
    private int periodNumber;
}
