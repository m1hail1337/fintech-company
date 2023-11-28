package com.academy.fintech.origination.application.db;

import com.academy.fintech.origination.application.exception.ApplicationAlreadyExistsException;
import com.academy.fintech.origination.application.exception.ApplicationNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repository;

    public String createApplication(String clientId, BigDecimal disbursementAmount) {
        Application createdApplication = Application.builder()
                .clientId(clientId)
                .disbursement(disbursementAmount)
                .build();
        checkDuplicateExistence(createdApplication);
        return saveApplication(createdApplication).getId();
    }

    public Application closeApplication(String applicationId) throws ApplicationNotExistsException {
        Optional<Application> existedApplication = repository.findById(applicationId);
        if (existedApplication.isEmpty()) {
            throw new ApplicationNotExistsException(applicationId);
        }
        Application applicationToClose = existedApplication.get();
        setClosedStatus(applicationToClose);
        if (applicationToClose.getStatus() == ApplicationStatus.CLOSED) {
            saveApplication(applicationToClose);
        }
        return applicationToClose;
    }

    private void checkDuplicateExistence(Application application) throws ApplicationAlreadyExistsException {
        String clientId = application.getClientId();
        BigDecimal disbursement = application.getDisbursement();
        ApplicationStatus status = application.getStatus();
        Optional<Application> queryResult = repository.findByClientIdAndDisbursementAndStatus(
                clientId,
                disbursement,
                status
        );

        if (queryResult.isPresent()) {
            throw new ApplicationAlreadyExistsException(queryResult.get().getId());
        }
    }

    @Transactional
    private Application saveApplication(Application application) {
        return repository.save(application);
    }

    private void setClosedStatus(Application application) {
        ApplicationStatus status = application.getStatus();
        if (status == ApplicationStatus.NEW || status == ApplicationStatus.SCORING) {
            application.setStatus(ApplicationStatus.CLOSED);
        }
    }
}
