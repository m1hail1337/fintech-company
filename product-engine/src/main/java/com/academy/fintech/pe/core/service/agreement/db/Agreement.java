package com.academy.fintech.pe.core.service.agreement.db;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Agreement")
@NoArgsConstructor
public class Agreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "interest")
    private BigDecimal interest;

    @Column(name = "term")
    private Integer term;

    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    @Column(name = "origination_amount")
    private BigDecimal originationAmount;

    @Column(name = "status")
    private String status;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;

    public Agreement(String product,
                     Integer clientID,
                     Integer monthTerm,
                     BigDecimal interest,
                     BigDecimal disbursement,
                     BigDecimal originationAmount) {
        this.clientId = clientID;
        this.term = monthTerm;
        this.productCode = product;
        this.interest = interest;
        this.principalAmount = disbursement.add(originationAmount);
        this.originationAmount = originationAmount;
        this.status = AgreementStatus.NEW.name();
        this.disbursementDate = null;
        this.nextPaymentDate = null;
    }
}
