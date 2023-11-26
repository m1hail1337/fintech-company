package com.academy.fintech.pe.grpc;

import com.academy.fintech.pe.AgreementRequest;
import com.academy.fintech.pe.AgreementResponse;
import com.academy.fintech.pe.DisbursementCreationRequest;
import com.academy.fintech.pe.DisbursementCreationResponse;
import com.academy.fintech.pe.ProductEngineServiceGrpc;
import com.academy.fintech.pe.core.service.agreement.AgreementCreationService;
import com.academy.fintech.pe.core.service.payment.DisbursementCreationService;
import com.academy.fintech.pe.grpc.dto.AgreementRecord;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

@GRpcService
@RequiredArgsConstructor
public class ProductEngineService extends ProductEngineServiceGrpc.ProductEngineServiceImplBase {

    private final AgreementCreationService agreementCreationService;

    private final DisbursementCreationService disbursementCreationService;

    @Override
    public void createAgreement(AgreementRequest request, StreamObserver<AgreementResponse> responseObserver) {
        AgreementRecord agreementToCreate = AgreementRecord.builder()
                .clientId(request.getClientId())
                .term(request.getLoanTerm())
                .disbursement(new BigDecimal(request.getDisbursementAmount()))
                .interest(new BigDecimal(request.getInterest()))
                .productCode(request.getProductCode())
                .build();
        Long agreementId = agreementCreationService.createAgreement(agreementToCreate);

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
