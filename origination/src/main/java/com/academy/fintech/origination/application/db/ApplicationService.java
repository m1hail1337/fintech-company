package com.academy.fintech.origination.application.db;

import com.academy.fintech.origination.application.exception.ApplicationAlreadyExistsException;
import com.academy.fintech.origination.application.exception.ApplicationNotExistsException;
import com.academy.fintech.origination.scoring.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ApplicationService {

    private final ScoringService scoringService;

    private final ApplicationRepository repository;

    @Transactional
    public String createApplication(String clientId, BigDecimal disbursementAmount) {
        Application createdApplication = Application.builder()
                .clientId(clientId)
                .disbursement(disbursementAmount)
                .build();
        checkDuplicateExistence(createdApplication);
        return saveApplication(createdApplication).getId();
    }

    @Transactional
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

    private Application saveApplication(Application application) {
        return repository.save(application);
    }

    private void setClosedStatus(Application application) {
        ApplicationStatus status = application.getStatus();
        if (status == ApplicationStatus.NEW || status == ApplicationStatus.SCORING) {
            application.setStatus(ApplicationStatus.CLOSED);
        }
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    void scoringNewApplications() {
        List<Application> newApplications = repository.findAllByStatus(ApplicationStatus.NEW);
        setScoringStatus(newApplications);
        for (Application application : newApplications) {
            int score = scoringService.scoreSolvency(application.getClientId(), application.getDisbursement());
            ApplicationStatus status = statusByScoringResult(score);
            application.setStatus(status);
            saveApplication(application);
            // Notifications.sendScoringResultEmail(application); - Not will be implemented
        }
    }

    private void setScoringStatus(List<Application> applications) {
        for (Application application : applications) {
            application.setStatus(ApplicationStatus.SCORING);
            saveApplication(application);
        }
    }

    private ApplicationStatus statusByScoringResult(int scoringResult) {
        if (scoringResult > 0) {
            return ApplicationStatus.ACCEPTED;
        }
        return ApplicationStatus.DECLINED;
    }
}
