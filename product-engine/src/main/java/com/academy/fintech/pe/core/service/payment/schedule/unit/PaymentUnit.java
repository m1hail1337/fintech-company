package com.academy.fintech.pe.core.service.payment.schedule.unit;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_schedule_unit")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class PaymentUnit {
    @EmbeddedId
    private final PaymentPk paymentPk;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentUnitStatus status = PaymentUnitStatus.FUTURE;

    @Column(name = "payment_date")
    private final LocalDate paymentDate;

    @Column(name = "period_payment")
    private final BigDecimal periodPayment;

    @Column(name = "interest_payment")
    private final BigDecimal interestPayment;

    @Column(name = "principal_payment")
    private final BigDecimal principalPayment;

    public void setStatus(PaymentUnitStatus status) {
        this.status = status;
    }
}
