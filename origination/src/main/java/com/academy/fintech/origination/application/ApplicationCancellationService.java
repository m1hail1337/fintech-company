package com.academy.fintech.origination.application;

import com.academy.fintech.origination.application.db.Application;
import com.academy.fintech.origination.application.db.ApplicationService;
import com.academy.fintech.origination.application.db.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationCancellationService {

    private final ApplicationService applicationService;

    public boolean cancelApplication(String applicationId) {
        Application application = applicationService.closeApplication(applicationId);
        return application.getStatus() == ApplicationStatus.CLOSED;
    }
}
