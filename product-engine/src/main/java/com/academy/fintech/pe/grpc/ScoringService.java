package com.academy.fintech.pe.grpc;

import com.academy.fintech.pe.LoansOverdueRequest;
import com.academy.fintech.pe.LoansOverdueResponse;
import com.academy.fintech.pe.MaxPaymentRequest;
import com.academy.fintech.pe.MaxPaymentResponse;
import com.academy.fintech.pe.ProductEngine4ScoringGrpc;
import com.academy.fintech.pe.core.service.payment.schedule.unit.PaymentUnitService;
import com.academy.fintech.pe.grpc.dto.AgreementDto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.math.BigDecimal;
import java.util.List;

@GRpcService
@RequiredArgsConstructor
public class ScoringService extends ProductEngine4ScoringGrpc.ProductEngine4ScoringImplBase {

    private final PaymentUnitService paymentUnitService;

    @Override
    public void getMaxPayment(MaxPaymentRequest request, StreamObserver<MaxPaymentResponse> responseObserver) {
        AgreementDto agreementDto = AgreementDto.builder()
                .term(request.getLoanTerm())
                .disbursement(new BigDecimal(request.getDisbursementAmount()))
                .interest(new BigDecimal(request.getInterest()))
                .build();
        BigDecimal maxPayment = paymentUnitService.getMaxPayment(agreementDto);
        responseObserver.onNext(MaxPaymentResponse.newBuilder()
                .setPaymentAmount(maxPayment.toString())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getLoansOverdue(LoansOverdueRequest request, StreamObserver<LoansOverdueResponse> responseObserver) {
        String clientId = request.getClientId();
        List<Integer> overdueDays = paymentUnitService.getLoansOverdueDays(clientId);
        responseObserver.onNext(LoansOverdueResponse.newBuilder()
                .addAllOverdueDays(overdueDays)
                .build());
        responseObserver.onCompleted();
    }
}
