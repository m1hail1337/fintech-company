package com.academy.fintech.scoring.pe.client;

import com.academy.fintech.pe.LoansOverdueRequest;
import com.academy.fintech.pe.LoansOverdueResponse;
import com.academy.fintech.pe.MaxPaymentRequest;
import com.academy.fintech.pe.MaxPaymentResponse;
import com.academy.fintech.scoring.pe.client.grpc.ProductEngineGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductEngineClientService {

    private final ProductEngineGrpcClient productEngineGrpcClient;

    public List<Integer> getLoansOverdue(String clientId) {
        LoansOverdueRequest request = LoansOverdueRequest.newBuilder().setClientId(clientId).build();
        LoansOverdueResponse response = productEngineGrpcClient.getLoansOverdue(request);
        return response.getOverdueDaysList();
    }

    public BigDecimal getMaxPayment(BigDecimal disbursement) {
        // Т.к. пока не понятно (мне) как мы получаем из origination длительность и процент кредита - хардкодим
        BigDecimal interest = BigDecimal.valueOf(0.08);
        int term = 12;
        MaxPaymentRequest request = MaxPaymentRequest.newBuilder()
                .setDisbursementAmount(disbursement.toString())
                .setInterest(interest.toString())
                .setLoanTerm(term)
                .build();
        MaxPaymentResponse response = productEngineGrpcClient.getMaxPayment(request);
        return new BigDecimal(response.getPaymentAmount());
    }
}
