package com.academy.fintech.origination.scoring.client;

import com.academy.fintech.origination.scoring.client.grpc.ScoringGrpcClient;
import com.academy.fintech.scoring.ScoreRequest;
import com.academy.fintech.scoring.ScoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ScoringClientService {

    private final ScoringGrpcClient scoringGrpcClient;

    public int scoreSolvency(String clientId, BigDecimal disbursement, BigDecimal salary) {
        ScoreRequest request = ScoreRequest.newBuilder()
                .setClientId(clientId)
                .setDisbursementAmount(disbursement.intValue())
                .setSalary(salary.intValue())
                .build();
        ScoreResponse response = scoringGrpcClient.scoreSolvency(request);
        return response.getSolvencyScore();
    }
}
