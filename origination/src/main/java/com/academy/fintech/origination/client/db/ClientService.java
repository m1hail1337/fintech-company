package com.academy.fintech.origination.client.db;

import com.academy.fintech.origination.grpc.dto.ClientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    public Optional<Client> findClientByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Client saveClient(ClientDto clientDto) {
        Client client = Client.builder()
                .firstName(clientDto.firstName())
                .lastName(clientDto.lastName())
                .email(clientDto.email())
                .salary(clientDto.salary())
                .build();
        return saveClient(client);
    }

    @Transactional
    private Client saveClient(Client client) {
        return repository.save(client);
    }
}
