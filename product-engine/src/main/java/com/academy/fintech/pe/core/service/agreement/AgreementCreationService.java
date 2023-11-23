package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.AgreementDAO;
import com.academy.fintech.pe.core.service.agreement.db.AgreementRepository;
import com.academy.fintech.pe.core.service.product.db.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AgreementCreationService {

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private ProductRepository productRepository;

    public Long createAgreement(int clientID,
                                int monthTerm,
                                BigDecimal disbursement,
                                BigDecimal interest,
                                String productCode) {
        // пока берем минимальный origination
        BigDecimal origination = productRepository.findById(productCode).orElseThrow().getMinOriginationAmount();
        AgreementDAO agreement = new AgreementDAO(productCode, clientID, monthTerm, interest, disbursement, origination);
        agreementRepository.save(agreement);
        return agreement.getId();
    }
}
