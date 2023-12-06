package com.academy.fintech.pe.core.service.agreement.db;

import com.academy.fintech.pe.core.service.product.db.Product;
import com.academy.fintech.pe.core.service.product.db.ProductRepository;
import com.academy.fintech.pe.grpc.dto.AgreementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgreementService {

    private final AgreementRepository agreementRepository;

    private final ProductRepository productRepository;

    @Transactional
    public Long createAgreement(AgreementDto agreementDto) {
        Product product = productRepository.findById(agreementDto.productCode()).orElseThrow();
        // пока берем минимальный origination
        BigDecimal origination = product.getMinOriginationAmount();
        BigDecimal principal = agreementDto.disbursement().add(origination);
        Agreement agreement = buildAgreement(agreementDto, principal, origination);
        agreementRepository.save(agreement);
        return agreement.getId();
    }

    public void saveAgreement(Agreement agreement) {
        agreementRepository.save(agreement);
    }

    public Optional<Agreement> findById(Long agreementId) {
        return agreementRepository.findById(agreementId);
    }

    private Agreement buildAgreement(AgreementDto agreementDto, BigDecimal principal, BigDecimal origination) {
        return Agreement.builder()
                .productCode(agreementDto.productCode())
                .clientId(agreementDto.clientId())
                .interest(agreementDto.interest())
                .term(agreementDto.term())
                .principalAmount(principal)
                .originationAmount(origination)
                .build();
    }
}
