package com.academy.fintech.pe.core.service.payment.schedule;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Payment_Schedule")
@NoArgsConstructor
public class PaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "agreement_number")
    private Long agreementNumber;

    @Column(name = "version")
    private Integer version;

    public PaymentSchedule(Long agreementNumber, Integer version) {
        this.agreementNumber = agreementNumber;
        this.version = version;
    }
}
