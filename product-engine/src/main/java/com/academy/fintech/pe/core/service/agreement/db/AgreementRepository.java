package com.academy.fintech.pe.core.service.agreement.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementRepository extends CrudRepository<Agreement, Long> {

}
