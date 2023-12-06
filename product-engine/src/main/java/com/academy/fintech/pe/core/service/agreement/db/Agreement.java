package com.academy.fintech.pe.core.service.agreement.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "agreement")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
public class Agreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_code")
    private final String productCode;

    @Column(name = "client_id")
    private final String clientId;

    @Column(name = "interest")
    private final BigDecimal interest;

    @Column(name = "term")
    private final Integer term;

    @Column(name = "principal_amount")
    private final BigDecimal principalAmount;

    @Column(name = "origination_amount")
    private final BigDecimal originationAmount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AgreementStatus status = AgreementStatus.NEW;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;
}
