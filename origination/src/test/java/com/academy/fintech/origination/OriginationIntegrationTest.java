package com.academy.fintech.origination;

import com.academy.fintech.DbContainer;
import com.academy.fintech.origination.application.ApplicationCancellationService;
import com.academy.fintech.origination.application.ApplicationCreationService;
import com.academy.fintech.origination.application.db.Application;
import com.academy.fintech.origination.application.db.ApplicationRepository;
import com.academy.fintech.origination.application.db.ApplicationStatus;
import com.academy.fintech.origination.application.exception.ApplicationAlreadyExistsException;
import com.academy.fintech.origination.application.exception.ApplicationNotExistsException;
import com.academy.fintech.origination.client.db.Client;
import com.academy.fintech.origination.client.db.ClientRepository;
import com.academy.fintech.origination.grpc.dto.ClientDto;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Testcontainers
@DirtiesContext // gRPC requires
@SpringBootTest
public class OriginationIntegrationTest {

    @Container
    static DbContainer databaseContainer = DbContainer.getInstance();

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", databaseContainer::getUsername);
        registry.add("spring.datasource.password", databaseContainer::getPassword);
    }

    List<String> clientIds = new ArrayList<>();
    List<String> applicationIds = new ArrayList<>();

    @Autowired
    ApplicationCreationService creationService;

    @Autowired
    ApplicationCancellationService cancellationService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @PostConstruct
    void setUp() {
        cleanRepositories();
        loadTestEntities();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testCreateApplicationForExistingClient() {
        Client client = clientRepository.findById(clientIds.get(0)).get();
        long clientsCountBeforeCreation = clientRepository.count();
        long applicationsCountBeforeCreation = applicationRepository.count();
        String createdApplicationId = creationService.createApplication(ClientDto.builder()
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .salary(client.getSalary())
                .email(client.getEmail())
                .build(), BigDecimal.valueOf(60000.0));
        applicationIds.add(createdApplicationId);
        Application createdApplication = applicationRepository.findById(createdApplicationId).get();
        assertNotNull(createdApplication);
        assertEquals(ApplicationStatus.NEW, createdApplication.getStatus());
        assertEquals(client.getId(), createdApplication.getClientId());
        assertEquals(clientsCountBeforeCreation, clientRepository.count());
        assertEquals(applicationsCountBeforeCreation + 1, applicationRepository.count());
    }

    @Test
    void testCreateApplicationForNotExistingClient() {
        long clientsCountBeforeCreation = clientRepository.count();
        String newClientEmail = "petrpetrov@mail.ru";
        String createdApplicationId = creationService.createApplication(ClientDto.builder()
                .firstName("petr")
                .lastName("petrov")
                .salary(BigDecimal.valueOf(65000.0))
                .email(newClientEmail)
                .build(), BigDecimal.valueOf(14000.0));
        applicationIds.add(createdApplicationId);
        Application createdApplication = applicationRepository.findById(createdApplicationId).get();
        assertNotNull(createdApplication);
        assertEquals(clientsCountBeforeCreation + 1, clientRepository.count());
        Client createdClient = clientRepository.findByEmail(newClientEmail).get();
        assertEquals("petr", createdClient.getFirstName());
        assertEquals(ApplicationStatus.NEW, createdApplication.getStatus());
        assertEquals(createdClient.getId(), createdApplication.getClientId());
    }

    @Test
    void testCreateApplicationDuplicate() {
        long applicationCountBeforeCreation = applicationRepository.count();
        String existingApplicationId = applicationIds.get(0);
        Application existingApplication = applicationRepository.findById(existingApplicationId).get();
        Client client = clientRepository.findById(existingApplication.getClientId()).get();
        ApplicationAlreadyExistsException exception = assertThrows(ApplicationAlreadyExistsException.class, () -> creationService.createApplication(
                ClientDto.builder()
                        .firstName(client.getFirstName())
                        .lastName(client.getLastName())
                        .email(client.getEmail())
                        .salary(client.getSalary())
                        .build(),
                existingApplication.getDisbursement()));
        assertEquals(existingApplicationId, exception.getExistedApplicationId());
        assertEquals(applicationCountBeforeCreation, applicationRepository.count());
    }

    @Test
    void cancelApplicationWithStatusNew() {
        String applicationToCancelId = applicationIds.get(0);
        assertTrue(cancellationService.cancelApplication(applicationToCancelId));
        Application canceledApplication = applicationRepository.findById(applicationToCancelId).get();
        assertEquals(ApplicationStatus.CLOSED, canceledApplication.getStatus());
    }

    @Test
    void cancelApplicationWithStatusActive() {
        String applicationToCancelId = applicationIds.get(1);
        assertFalse(cancellationService.cancelApplication(applicationToCancelId));
        Application canceledApplication = applicationRepository.findById(applicationToCancelId).get();
        assertEquals(ApplicationStatus.ACTIVE, canceledApplication.getStatus());
    }

    @Test
    void cancelNotExistingApplication() {
        long applicationCountBeforeCanceling = applicationRepository.count();
        String applicationToCancelId = "some-not-existing-id";
        assertFalse(applicationRepository.existsById(applicationToCancelId));
        ApplicationNotExistsException exception = assertThrows(ApplicationNotExistsException.class, () ->
                cancellationService.cancelApplication(applicationToCancelId)
        );
        assertEquals(applicationToCancelId, exception.getNotExistedApplicationId());
        assertEquals(applicationCountBeforeCanceling, applicationRepository.count());
    }

    private void cleanRepositories() {
        applicationRepository.deleteAll();
        clientRepository.deleteAll();
    }

    private void loadTestEntities() {
        loadTwoClients();
        loadTwoApplicationsForLoadedClients();
    }

    private void loadTwoClients() {
        clientIds.add(clientRepository.save(Client.builder()
                .firstName("mihail")
                .lastName("semenov")
                .email("example@mail.ru")
                .salary(BigDecimal.valueOf(1000000.0))
                .build()).getId()
        );
        clientIds.add(clientRepository.save(Client.builder()
                .firstName("ivan")
                .lastName("ivanov")
                .email("some@mail.ru")
                .salary(BigDecimal.valueOf(10000.0))
                .build()).getId()
        );
    }

    private void loadTwoApplicationsForLoadedClients() {
        applicationIds.add(applicationRepository.save(Application.builder()
                .clientId(clientIds.get(0))
                .status(ApplicationStatus.NEW)
                .disbursement(BigDecimal.valueOf(30000.0))
                .build()).getId()
        );
        applicationIds.add(applicationRepository.save(Application.builder()
                .clientId(clientIds.get(1))
                .status(ApplicationStatus.ACTIVE)
                .disbursement(BigDecimal.valueOf(5000.0))
                .build()).getId()
        );
    }
}
