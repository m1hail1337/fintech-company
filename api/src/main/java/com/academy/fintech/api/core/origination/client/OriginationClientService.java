package com.academy.fintech.api.core.origination.client;

import com.academy.fintech.api.core.origination.client.grpc.OriginationGrpcClient;
import com.academy.fintech.api.public_interface.application.dto.ApplicationDto;

import com.academy.fintech.origination.CreationRequest;
import com.academy.fintech.origination.CreationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OriginationClientService {

    private final OriginationGrpcClient originationGrpcClient;

    public String createApplication(ApplicationDto applicationDto) {
        CreationRequest request = mapDtoToRequest(applicationDto);

        CreationResponse response = originationGrpcClient.createApplication(request);

        return response.getApplicationId();
    }

    private static CreationRequest mapDtoToRequest(ApplicationDto applicationDto) {
        return CreationRequest.newBuilder()
                .setFirstName(applicationDto.firstName())
                .setLastName(applicationDto.lastName())
                .setEmail(applicationDto.email())
                .setSalary(applicationDto.salary())
                .setDisbursementAmount(applicationDto.amount())
                .build();
    }

}
