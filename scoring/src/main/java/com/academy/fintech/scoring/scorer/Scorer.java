package com.academy.fintech.scoring.scorer;

import com.academy.fintech.scoring.pe.ProductEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Scorer {

    private final ProductEngineService productEngineService;

    public int getTotalScore(String clientId, BigDecimal disbursement, BigDecimal salary) {
        return getSalaryScore(disbursement, salary) + getCreditHistoryScore(clientId);
    }

    private int getSalaryScore(BigDecimal disbursement, BigDecimal salary) {
        BigDecimal maxPayment = productEngineService.getMaxPayment(disbursement);
        if (salary.compareTo(maxPayment.multiply(BigDecimal.valueOf(3))) >= 0) {
            return 1;
        }
        return 0;
    }

    private int getCreditHistoryScore(String clientId) {
        List<Integer> overdueDays = productEngineService.getLoansOverdue(clientId);
        if (overdueDays.isEmpty()) {
            return 1;
        } else if (overdueDays.stream().anyMatch(day -> day > 7)) {
            return -1;
        }
        return 0;
    }
}
