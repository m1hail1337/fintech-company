package com.academy.fintech.pe.grpc.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AgreementDto(
        String clientId,
        int term,
        BigDecimal disbursement,
        BigDecimal interest,
        String productCode) {}
