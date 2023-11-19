package com.academy.fintech.pe.core.service.payment.schedule.unit;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@IdClass(PaymentPK.class)
@Table(name = "Payment_Schedule_Unit")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentUnit {
    @Id
    @Column(name = "payment_schedule_id")
    private Long scheduleId;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "period_payment")
    private BigDecimal periodPayment;

    @Column(name = "interest_payment")
    private BigDecimal interestPayment;

    @Column(name = "principal_payment")
    private BigDecimal principalPayment;

    @Id
    @Column(name = "period_number")
    private Integer periodNumber;
}
