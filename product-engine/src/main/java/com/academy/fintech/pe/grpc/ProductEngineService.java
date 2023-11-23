package com.academy.fintech.pe.grpc;

import com.academy.fintech.pe.AgreementRequest;
import com.academy.fintech.pe.AgreementResponse;
import com.academy.fintech.pe.DisbursementCreationRequest;
import com.academy.fintech.pe.DisbursementCreationResponse;
import com.academy.fintech.pe.ProductEngineServiceGrpc;
import com.academy.fintech.pe.core.service.agreement.AgreementCreationService;
import com.academy.fintech.pe.core.service.payment.DisbursementCreationService;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

@GRpcService
public class ProductEngineService extends ProductEngineServiceGrpc.ProductEngineServiceImplBase {

    @Autowired
    AgreementCreationService agreementCreationService;

    @Autowired
    DisbursementCreationService disbursementCreationService;

    @Override
    public void createAgreement(AgreementRequest request, StreamObserver<AgreementResponse> responseObserver) {
        int clientId = request.getClientId();
        int term = request.getLoanTerm();
        BigDecimal disbursement = new BigDecimal(request.getDisbursementAmount());
        BigDecimal interest = new BigDecimal(request.getInterest());
        String productCode = request.getProductCode();
        Long agreementId = agreementCreationService.createAgreement(
                clientId, term, disbursement, interest, productCode
        );

        responseObserver.onNext(
                AgreementResponse.newBuilder()
                        .setAgreementId(agreementId)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void createDisbursement(DisbursementCreationRequest request,
                                   StreamObserver<DisbursementCreationResponse> responseObserver) {
        long agreementId = request.getAgreementId();
        LocalDate disbursementDate = LocalDate.parse(request.getDisbursementDate());
        Long scheduleId = disbursementCreationService.createSchedule(agreementId, disbursementDate);
        responseObserver.onNext(
                DisbursementCreationResponse.newBuilder()
                        .setPaymentScheduleId(scheduleId)
                        .build()
        );
        responseObserver.onCompleted();
    }
}
