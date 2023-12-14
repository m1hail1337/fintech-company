package com.academy.fintech.scoring.pe.client.grpc;

import com.academy.fintech.pe.LoansOverdueRequest;
import com.academy.fintech.pe.LoansOverdueResponse;
import com.academy.fintech.pe.MaxPaymentRequest;
import com.academy.fintech.pe.MaxPaymentResponse;
import com.academy.fintech.pe.ProductEngine4ScoringGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

@Component
public class ProductEngineGrpcClient {

    private final ProductEngine4ScoringGrpc.ProductEngine4ScoringBlockingStub stub;

    public ProductEngineGrpcClient(ProductEngineGrpcClientProperty property) {
        Channel channel = ManagedChannelBuilder.forAddress(property.host(), property.port()).usePlaintext().build();
        this.stub = ProductEngine4ScoringGrpc.newBlockingStub(channel);
    }

    public MaxPaymentResponse getMaxPayment(MaxPaymentRequest maxPaymentRequest) {
        try {
            return stub.getMaxPayment(maxPaymentRequest);
        } catch (StatusRuntimeException e) {
            throw e;
        }
    }

    public LoansOverdueResponse getLoansOverdue(LoansOverdueRequest loansOverdueRequest) {
        try {
            return stub.getLoansOverdue(loansOverdueRequest);
        } catch (StatusRuntimeException e) {
            throw e;
        }
    }
}
