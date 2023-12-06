package com.academy.fintech.origination.application;

import com.academy.fintech.origination.application.db.ApplicationService;
import com.academy.fintech.origination.client.db.Client;
import com.academy.fintech.origination.client.db.ClientService;
import com.academy.fintech.origination.grpc.dto.ClientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationCreationService {

    private final ApplicationService applicationService;
    private final ClientService clientService;

    public String createApplication(ClientDto clientDto, BigDecimal disbursement) {
        String clientId;
        Optional<Client> clientQuery = clientService.findClientByEmail(clientDto.email());
        if (clientQuery.isEmpty()) {
            Client savedClient = clientService.saveClient(clientDto);
            clientId = savedClient.getId();
        } else {
            clientId = clientQuery.get().getId();
        }
        return applicationService.createApplication(clientId, disbursement);
    }
}
