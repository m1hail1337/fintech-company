package com.academy.fintech.origination.application.db;

import com.academy.fintech.origination.application.exception.ApplicationAlreadyExistsException;
import com.academy.fintech.origination.application.exception.ApplicationNotExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
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
public class ApplicationServiceTest {

    @MockBean
    private ApplicationRepository repository;

    @Autowired
    private ApplicationService service;

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
        String actualId = service.createApplication(clientId, disbursement);
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
                service.createApplication(clientId, disbursement)
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
        Application closedApplication = service.closeApplication("app1");
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
        Application closedApplication = service.closeApplication("app1");
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
        Application closedApplication = service.closeApplication("app1");
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
                service.closeApplication(notExistedId)
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
        Application closedApplication = service.closeApplication("app1");
        verify(repository, times(1)).save(any());
        assertEquals(existed.getId(), closedApplication.getId());
        assertEquals(existed.getClientId(), closedApplication.getClientId());
        assertEquals(existed.getDisbursement(), closedApplication.getDisbursement());
        assertEquals(ApplicationStatus.CLOSED, closedApplication.getStatus());
    }
}
