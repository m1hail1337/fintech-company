package com.academy.fintech.pe.core.service.payment.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "payment_schedule")
@NoArgsConstructor
@AllArgsConstructor
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
