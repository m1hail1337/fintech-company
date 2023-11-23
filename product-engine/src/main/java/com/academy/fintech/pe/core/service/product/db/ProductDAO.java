package com.academy.fintech.pe.core.service.product.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Product")
@AllArgsConstructor
@NoArgsConstructor
public class ProductDAO {
    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "min_term")
    private Integer minTerm;

    @Column(name = "max_term")
    private Integer maxTerm;

    @Column(name = "min_principal_amount")
    private BigDecimal minPrincipalAmount;

    @Column(name = "max_principal_amount")
    private BigDecimal maxPrincipalAmount;

    @Column(name = "min_interest")
    private BigDecimal minInterest;

    @Column(name = "max_interest")
    private BigDecimal maxInterest;

    @Column(name = "min_origination_amount")
    private BigDecimal minOriginationAmount;

    @Column(name = "max_origination_amount")
    private BigDecimal maxOriginationAmount;
}
