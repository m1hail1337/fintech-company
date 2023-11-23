package com.academy.fintech.pe.core.service.agreement.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementRepository extends CrudRepository<AgreementDAO, Long> {
    List<AgreementDAO> findByClientId(Integer clientId);
}
