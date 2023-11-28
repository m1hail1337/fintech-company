package com.academy.fintech.origination.application.db;

import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface ApplicationRepository extends CrudRepository<Application, String> {

    Optional<Application> findByClientIdAndDisbursementAndStatus(String clientId,
                                                    BigDecimal requestedDisbursement,
                                                    ApplicationStatus status);

}
