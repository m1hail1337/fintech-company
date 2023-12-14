package com.academy.fintech.scoring.grpc;

import com.academy.fintech.scoring.ScoreRequest;
import com.academy.fintech.scoring.ScoreResponse;
import com.academy.fintech.scoring.ScoringServiceGrpc;
import com.academy.fintech.scoring.scorer.Scorer;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.math.BigDecimal;

@GRpcService
@RequiredArgsConstructor
public class ScoringService extends ScoringServiceGrpc.ScoringServiceImplBase {

    private final Scorer scorer;

    @Override
    public void scoreSolvency(ScoreRequest request, StreamObserver<ScoreResponse> responseObserver) {
        String clientId = request.getClientId();
        BigDecimal disbursement = BigDecimal.valueOf(request.getDisbursementAmount());
        BigDecimal salary = BigDecimal.valueOf(request.getSalary());
        int totalScore = scorer.getTotalScore(clientId, disbursement, salary);

        responseObserver.onNext(ScoreResponse.newBuilder()
                .setSolvencyScore(totalScore)
                .build()
        );
        responseObserver.onCompleted();
    }

}
