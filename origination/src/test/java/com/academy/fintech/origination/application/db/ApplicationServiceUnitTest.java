package com.academy.fintech.origination.application.db;

import com.academy.fintech.origination.application.exception.ApplicationAlreadyExistsException;
import com.academy.fintech.origination.application.exception.ApplicationNotExistsException;
import com.academy.fintech.origination.scoring.ScoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@DirtiesContext // gRPC requires
@SpringBootTest
public class ApplicationServiceUnitTest {

    @MockBean
    ApplicationRepository repository;

    @MockBean
    ScoringService scoringService;

    @Autowired
    ApplicationService applicationService;

    @Test
    void testCreateApplication() {
        String clientId = "client-1";
        BigDecimal disbursement = new BigDecimal("10000");
        Application expected = Application.builder()
                .id("app1")
                .clientId(clientId)
                .disbursement(disbursement)
                .status(ApplicationStatus.NEW)
                .build();
        when(repository.findByClientIdAndDisbursementAndStatus(clientId, disbursement, ApplicationStatus.NEW))
                .thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(expected);
        String actualId = applicationService.createApplication(clientId, disbursement);
        verify(repository, times(1)).save(any());
        assertEquals(expected.getId(), actualId);
    }

    @Test
    void testCreateDuplicateApplication() {
        String clientId = "client-1";
        BigDecimal disbursement = new BigDecimal("10000");
        Application existed = Application.builder()
                .id("app1")
                .clientId(clientId)
                .disbursement(disbursement)
                .status(ApplicationStatus.NEW)
                .build();
        when(repository.findByClientIdAndDisbursementAndStatus(clientId, disbursement, ApplicationStatus.NEW))
                .thenReturn(Optional.of(existed));
        ApplicationAlreadyExistsException e = assertThrows(ApplicationAlreadyExistsException.class, () ->
                applicationService.createApplication(clientId, disbursement)
        );
        verify(repository, never()).save(any());
        assertEquals(existed.getId(), e.getExistedApplicationId());
    }

    @Test
    void testCloseApplicationWithNewStatus() {
        ApplicationStatus status = ApplicationStatus.NEW;
        Application existed = Application.builder()
                .id("app1")
                .clientId("client1")
                .status(status)
                .build();
        when(repository.findById("app1")).thenReturn(Optional.of(existed));
        Application closedApplication = applicationService.closeApplication("app1");
        verify(repository, times(1)).save(any());
        assertEquals(existed.getId(), closedApplication.getId());
        assertEquals(existed.getClientId(), closedApplication.getClientId());
        assertEquals(existed.getDisbursement(), closedApplication.getDisbursement());
        assertEquals(ApplicationStatus.CLOSED, closedApplication.getStatus());
    }

    @Test
    void testCloseApplicationWithScoringStatus() {
        ApplicationStatus status = ApplicationStatus.SCORING;
        Application existed = Application.builder()
                .id("app1")
                .clientId("client1")
                .status(status)
                .build();
        when(repository.findById("app1")).thenReturn(Optional.of(existed));
        Application closedApplication = applicationService.closeApplication("app1");
        verify(repository, times(1)).save(any());
        assertEquals(existed.getId(), closedApplication.getId());
        assertEquals(existed.getClientId(), closedApplication.getClientId());
        assertEquals(existed.getDisbursement(), closedApplication.getDisbursement());
        assertEquals(ApplicationStatus.CLOSED, closedApplication.getStatus());
    }

    @Test
    void testCloseApplicationWithActiveStatus() {
        ApplicationStatus status = ApplicationStatus.ACTIVE;
        Application existed = Application.builder()
                .id("app1")
                .clientId("client1")
                .status(status)
                .build();
        when(repository.findById("app1")).thenReturn(Optional.of(existed));
        Application closedApplication = applicationService.closeApplication("app1");
        verify(repository, never()).save(any());
        assertEquals(existed.getId(), closedApplication.getId());
        assertEquals(existed.getClientId(), closedApplication.getClientId());
        assertEquals(existed.getDisbursement(), closedApplication.getDisbursement());
        assertEquals(ApplicationStatus.ACTIVE, closedApplication.getStatus());
    }

    @Test
    void testCloseNotExistedApplication() {
        String notExistedId = "some-not-existed-app-id";
        when(repository.findById(any())).thenReturn(Optional.empty());
        ApplicationNotExistsException exception = assertThrows(ApplicationNotExistsException.class, () ->
                applicationService.closeApplication(notExistedId)
        );
        verify(repository, never()).save(any());
        assertEquals(notExistedId, exception.getNotExistedApplicationId());
    }

    @Test
    void testCloseAlreadyClosedApplication() {
        ApplicationStatus status = ApplicationStatus.CLOSED;
        Application existed = Application.builder()
                .id("app1")
                .clientId("client1")
                .status(status)
                .build();
        when(repository.findById("app1")).thenReturn(Optional.of(existed));
        Application closedApplication = applicationService.closeApplication("app1");
        verify(repository, times(1)).save(any());
        assertEquals(existed.getId(), closedApplication.getId());
        assertEquals(existed.getClientId(), closedApplication.getClientId());
        assertEquals(existed.getDisbursement(), closedApplication.getDisbursement());
        assertEquals(ApplicationStatus.CLOSED, closedApplication.getStatus());
    }

    @Test
    void testScoringNewApplications() {
        List<Application> newApplications = createListOfSomeNewApplications();
        Application application1 = newApplications.get(0);
        Application application2 = newApplications.get(1);
        Application application3 = newApplications.get(2);
        Application application4 = newApplications.get(2);
        when(repository.findAllByStatus(ApplicationStatus.NEW)).thenReturn(newApplications);
        when(scoringService.scoreSolvency(application1.getClientId(), application1.getDisbursement())).thenReturn(2);
        when(scoringService.scoreSolvency(application2.getClientId(), application2.getDisbursement())).thenReturn(1);
        when(scoringService.scoreSolvency(application3.getClientId(), application3.getDisbursement())).thenReturn(0);
        when(scoringService.scoreSolvency(application4.getClientId(), application4.getDisbursement())).thenReturn(-1);
        applicationService.scoringNewApplications();
        assertEquals(ApplicationStatus.ACCEPTED, application1.getStatus());
        assertEquals(ApplicationStatus.ACCEPTED, application2.getStatus());
        assertEquals(ApplicationStatus.DECLINED, application3.getStatus());
        assertEquals(ApplicationStatus.DECLINED, application4.getStatus());
    }

    private List<Application> createListOfSomeNewApplications() {
        Application application1 = Application.builder()
                .id("app1")
                .clientId("client1")
                .disbursement(BigDecimal.valueOf(10000))
                .status(ApplicationStatus.NEW)
                .build();
        Application application2 = Application.builder()
                .id("app3")
                .clientId("client2")
                .disbursement(BigDecimal.valueOf(12315))
                .status(ApplicationStatus.NEW)
                .build();
        Application application3 = Application.builder()
                .id("app3")
                .clientId("client2")
                .disbursement(BigDecimal.valueOf(99894))
                .status(ApplicationStatus.NEW)
                .build();
        Application application4 = Application.builder()
                .id("app4")
                .clientId("client3")
                .disbursement(BigDecimal.valueOf(99894))
                .status(ApplicationStatus.NEW)
                .build();
        return List.of(application1, application2, application3, application4);
    }
}
