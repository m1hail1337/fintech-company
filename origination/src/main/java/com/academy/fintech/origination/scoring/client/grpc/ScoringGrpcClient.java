package com.academy.fintech.origination.scoring.client.grpc;

import com.academy.fintech.scoring.ScoreRequest;
import com.academy.fintech.scoring.ScoreResponse;
import com.academy.fintech.scoring.ScoringServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

@Component
public class ScoringGrpcClient {

    private final ScoringServiceGrpc.ScoringServiceBlockingStub stub;

    public ScoringGrpcClient(ScoringGrpcClientProperty property) {
        Channel channel = ManagedChannelBuilder.forAddress(property.host(), property.port()).usePlaintext().build();
        this.stub = ScoringServiceGrpc.newBlockingStub(channel);
    }

    public ScoreResponse scoreSolvency(ScoreRequest scoreRequest) {
        try {
            return stub.scoreSolvency(scoreRequest);
        } catch (StatusRuntimeException e) {
            throw e;
        }
    }
}
