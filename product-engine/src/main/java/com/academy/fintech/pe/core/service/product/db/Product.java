package com.academy.fintech.pe.core.service.product.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
public class Product {
    @Id
    @Column(name = "code")
    private final String code;

    @Column(name = "min_term")
    private final Integer minTerm;

    @Column(name = "max_term")
    private final Integer maxTerm;

    @Column(name = "min_principal_amount")
    private final BigDecimal minPrincipalAmount;

    @Column(name = "max_principal_amount")
    private final BigDecimal maxPrincipalAmount;

    @Column(name = "min_interest")
    private final BigDecimal minInterest;

    @Column(name = "max_interest")
    private final BigDecimal maxInterest;

    @Column(name = "min_origination_amount")
    private final BigDecimal minOriginationAmount;

    @Column(name = "max_origination_amount")
    private final BigDecimal maxOriginationAmount;
}
