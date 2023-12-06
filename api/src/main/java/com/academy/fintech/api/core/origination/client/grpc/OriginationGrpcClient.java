package com.academy.fintech.api.core.origination.client.grpc;

import com.academy.fintech.origination.OriginationServiceGrpc;
import com.academy.fintech.origination.CreationRequest;
import com.academy.fintech.origination.CreationResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OriginationGrpcClient {

    private final OriginationServiceGrpc.OriginationServiceBlockingStub stub;

    public OriginationGrpcClient(OriginationGrpcClientProperty property) {
        Channel channel = ManagedChannelBuilder.forAddress(property.host(), property.port()).usePlaintext().build();
        this.stub = OriginationServiceGrpc.newBlockingStub(channel);
    }

    public CreationResponse createApplication(CreationRequest applicationRequest) {
        try {
            return stub.createApplication(applicationRequest);
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.ALREADY_EXISTS && e.getTrailers() != null) {
                String applicationId = getExistedApplicationIdFromTrailers(e.getTrailers());
                return buildApplicationResponse(applicationId);
            }
            log.error("Got error from Origination by request: {}", applicationRequest, e);
            throw e;
        }
    }

    private String getExistedApplicationIdFromTrailers(Metadata trailers) {
        return trailers.get(Metadata.Key.of("application-id", Metadata.ASCII_STRING_MARSHALLER));
    }

    private CreationResponse buildApplicationResponse(String applicationId) {
        return CreationResponse.newBuilder().setApplicationId(applicationId).build();
    }

}
