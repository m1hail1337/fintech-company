package com.academy.fintech.origination.client.db;

import com.academy.fintech.origination.grpc.dto.ClientDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DirtiesContext // gRPC requires
@SpringBootTest
public class ClientServiceUnitTest {

    @MockBean
    private ClientRepository repository;

    @Autowired
    private ClientService service;

    @Test
    void testFindExistedClientByEmail() {
        String existedClientEmail = "existed@mail.com";
        Client existedClient = Client.builder()
                .id("client-1")
                .email(existedClientEmail)
                .firstName("name")
                .lastName("lastname")
                .salary(BigDecimal.valueOf(10000))
                .build();
        when(repository.findByEmail(existedClientEmail)).thenReturn(Optional.of(existedClient));
        Optional<Client> actual = service.findClientByEmail(existedClientEmail);
        assertTrue(actual.isPresent());
        assertEquals(existedClient, actual.get());
    }

    @Test
    void testFindNotExistedClientByEmail() {
        String notExistedClientEmail = "not-existed@mail.com";
        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        Optional<Client> actual = service.findClientByEmail(notExistedClientEmail);
        assertTrue(actual.isEmpty());
    }

    @Test
    void testSaveClientWithDto() {
        String expectedId = "exp-id";
        ClientDto clientDto = ClientDto.builder()
                .firstName("name")
                .lastName("lastname")
                .email("some@mail.com")
                .salary(BigDecimal.valueOf(1000000))
                .build();
        when(repository.save(any())).thenAnswer(invocation -> {
           Client savedClient = invocation.getArgument(0);
           savedClient.setId(expectedId);
           return savedClient;
        });
        Client actualClient = service.saveClient(clientDto);
        verify(repository, times(1)).save(any());
        assertEquals(expectedId, actualClient.getId());
        assertEquals(clientDto.firstName(), actualClient.getFirstName());
        assertEquals(clientDto.lastName(), actualClient.getLastName());
        assertEquals(clientDto.email(), actualClient.getEmail());
        assertEquals(clientDto.salary(), actualClient.getSalary());
    }
}
