package com.academy.fintech.scoring.pe;

import com.academy.fintech.scoring.pe.client.ProductEngineClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductEngineService {

    private final ProductEngineClientService productEngineClientService;

    public List<Integer> getLoansOverdue(String clientId) {
        return productEngineClientService.getLoansOverdue(clientId);
    }

    public BigDecimal getMaxPayment(BigDecimal disbursement) {
        return productEngineClientService.getMaxPayment(disbursement);
    }
}
