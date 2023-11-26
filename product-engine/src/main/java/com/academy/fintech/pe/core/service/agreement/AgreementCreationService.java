package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.product.db.Product;
import com.academy.fintech.pe.core.service.product.db.ProductRepository;
import com.academy.fintech.pe.grpc.dto.AgreementRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AgreementCreationService {

    private final AgreementRepository agreementRepository;

    private final ProductRepository productRepository;

    public Long createAgreement(AgreementRecord agreementInput) {
        Product product = productRepository.findById(agreementInput.productCode()).orElseThrow();
        // пока берем минимальный origination
        BigDecimal origination = product.getMinOriginationAmount();
        BigDecimal principal = agreementInput.disbursement().add(origination);
        Agreement agreement = Agreement.builder()
                .productCode(agreementInput.productCode())
                .clientId(agreementInput.clientId())
                .interest(agreementInput.interest())
                .term(agreementInput.term())
                .principalAmount(principal)
                .originationAmount(origination)
                .build();
        agreementRepository.save(agreement);
        return agreement.getId();
    }
}
