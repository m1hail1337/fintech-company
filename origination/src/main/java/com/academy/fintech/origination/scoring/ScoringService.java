package com.academy.fintech.origination.scoring;

import com.academy.fintech.origination.client.db.ClientService;
import com.academy.fintech.origination.scoring.client.ScoringClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final ScoringClientService scoringClientService;

    private final ClientService clientService;

    public int scoreSolvency(String clientId, BigDecimal disbursement) {
        BigDecimal salary = clientService.getSalaryByClientId(clientId).orElseThrow();
        return scoringClientService.scoreSolvency(clientId, disbursement, salary);
    }
}
