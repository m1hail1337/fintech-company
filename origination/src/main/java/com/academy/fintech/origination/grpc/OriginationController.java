package com.academy.fintech.origination.grpc;

import com.academy.fintech.origination.CancellationRequest;
import com.academy.fintech.origination.CancellationResponse;
import com.academy.fintech.origination.CreationRequest;
import com.academy.fintech.origination.CreationResponse;
import com.academy.fintech.origination.OriginationServiceGrpc;
import com.academy.fintech.origination.application.ApplicationCancellationService;
import com.academy.fintech.origination.application.ApplicationCreationService;
import com.academy.fintech.origination.application.exception.ApplicationAlreadyExistsException;
import com.academy.fintech.origination.application.exception.ApplicationNotExistsException;
import com.academy.fintech.origination.grpc.dto.ClientDto;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.math.BigDecimal;

@GRpcService
@RequiredArgsConstructor
public class OriginationController extends OriginationServiceGrpc.OriginationServiceImplBase {

    private final ApplicationCreationService creationService;
    private final ApplicationCancellationService cancellationService;

    @Override
    public void createApplication(CreationRequest request, StreamObserver<CreationResponse> responseObserver) {
        ClientDto clientDto = buildClientDto(request);
        BigDecimal disbursementAmount = BigDecimal.valueOf(request.getDisbursementAmount());
        try {
            sendCreatedApplicationId(clientDto, disbursementAmount, responseObserver);
        } catch (ApplicationAlreadyExistsException e) {
            sendExistingApplicationIdInTrailers(e, responseObserver);
        }
    }

    @Override
    public void cancelApplication(CancellationRequest request, StreamObserver<CancellationResponse> responseObserver) {
        String cancelApplicationId = request.getApplicationId();
        try {
            sendSuccessOfCancellation(cancelApplicationId, responseObserver);
        } catch (ApplicationNotExistsException e) {
            sendErrorIfApplicationNotExists(e, responseObserver);
        }
    }

    private ClientDto buildClientDto(CreationRequest request) {
        return ClientDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .salary(BigDecimal.valueOf(request.getSalary()))
                .build();
    }

    private void sendCreatedApplicationId(ClientDto clientDto,
                                          BigDecimal disbursementAmount,
                                          StreamObserver<CreationResponse> responseObserver) {
        String applicationId = creationService.createApplication(clientDto, disbursementAmount);
        responseObserver.onNext(
                CreationResponse.newBuilder()
                        .setApplicationId(applicationId)
                        .build()
        );
        responseObserver.onCompleted();
    }

    private void sendExistingApplicationIdInTrailers(ApplicationAlreadyExistsException e,
                                                     StreamObserver<CreationResponse> responseObserver) {
        Metadata trailers = new Metadata();
        trailers.put(
                Metadata.Key.of("application-id", Metadata.ASCII_STRING_MARSHALLER),
                e.getExistedApplicationId()
        );
        StatusException alreadyExistsStatus = Status.ALREADY_EXISTS
                .withDescription(e.getMessage())
                .asException(trailers);
        responseObserver.onError(alreadyExistsStatus);
    }

    private void sendSuccessOfCancellation(String cancelApplicationId,
                                           StreamObserver<CancellationResponse> responseObserver) {
        boolean canceledSuccessfully = cancellationService.cancelApplication(cancelApplicationId);
        responseObserver.onNext(
                CancellationResponse.newBuilder()
                        .setIsCanceled(canceledSuccessfully)
                        .build()
        );
        responseObserver.onCompleted();
    }

    private void sendErrorIfApplicationNotExists(ApplicationNotExistsException e,
                                                 StreamObserver<CancellationResponse> responseObserver) {
        Metadata trailers = new Metadata();
        trailers.put(
                Metadata.Key.of("application-id", Metadata.ASCII_STRING_MARSHALLER),
                e.getNotExistedApplicationId()
        );
        StatusException notFoundStatus = Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asException(trailers);
        responseObserver.onError(notFoundStatus);
    }
}
