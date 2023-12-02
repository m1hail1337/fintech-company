package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.AgreementService;
import com.academy.fintech.pe.grpc.dto.AgreementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AgreementCreationService {

    private final AgreementService agreementService;

    public Long createAgreement(AgreementDto agreementDto) {
        return agreementService.createAgreement(agreementDto);
    }
}
