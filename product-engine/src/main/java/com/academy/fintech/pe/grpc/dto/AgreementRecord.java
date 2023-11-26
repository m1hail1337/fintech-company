package com.academy.fintech.pe.grpc.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
// TODO: Dto для передачи между сервисами
public record AgreementRecord(long clientId, int term, BigDecimal disbursement, BigDecimal interest, String productCode)
{}
