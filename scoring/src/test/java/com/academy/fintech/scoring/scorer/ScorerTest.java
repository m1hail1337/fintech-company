package com.academy.fintech.scoring.scorer;

import com.academy.fintech.scoring.pe.ProductEngineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ScorerTest {

    @MockBean
    ProductEngineService productEngineService;

    @Autowired
    Scorer scorer;

    @Test
    void testTotalScoreGoodSalaryAndCreditHistoryWithoutOverdue() {
        String clientId = "client-1";
        BigDecimal disbursement = BigDecimal.valueOf(60000);
        BigDecimal salary = BigDecimal.valueOf(50000);
        when(productEngineService.getMaxPayment(disbursement)).thenReturn(BigDecimal.valueOf(5000));
        when(productEngineService.getLoansOverdue(clientId)).thenReturn(List.of());
        int score = scorer.getTotalScore(clientId, disbursement, salary);
        assertEquals(2, score);
    }

    @Test
    void testTotalScoreGoodSalaryAndCreditHistoryCriticOverdue() {
        String clientId = "client-1";
        List<Integer> overdueDays = List.of(13, 41, 14);
        BigDecimal disbursement = BigDecimal.valueOf(12000);
        BigDecimal salary = BigDecimal.valueOf(3001);
        when(productEngineService.getMaxPayment(disbursement)).thenReturn(BigDecimal.valueOf(1000));
        when(productEngineService.getLoansOverdue(clientId)).thenReturn(overdueDays);
        int score = scorer.getTotalScore(clientId, disbursement, salary);
        assertEquals(0, score);
    }

    @Test
    void testTotalScoreGoodSalaryAndCreditHistoryNotCriticOverdue() {
        String clientId = "client-1";
        List<Integer> overdueDays = List.of(3, 1);
        BigDecimal disbursement = BigDecimal.valueOf(36000);
        BigDecimal salary = BigDecimal.valueOf(10000);
        when(productEngineService.getMaxPayment(disbursement)).thenReturn(BigDecimal.valueOf(3000));
        when(productEngineService.getLoansOverdue(clientId)).thenReturn(overdueDays);
        int score = scorer.getTotalScore(clientId, disbursement, salary);
        assertEquals(1, score);
    }

    @Test
    void testTotalScoreBadSalaryAndCreditHistoryWithoutOverdue() {
        String clientId = "client-1";
        BigDecimal disbursement = BigDecimal.valueOf(120000);
        BigDecimal salary = BigDecimal.valueOf(29999);
        when(productEngineService.getMaxPayment(disbursement)).thenReturn(BigDecimal.valueOf(10000));
        when(productEngineService.getLoansOverdue(clientId)).thenReturn(List.of());
        int score = scorer.getTotalScore(clientId, disbursement, salary);
        assertEquals(1, score);
    }

    @Test
    void testTotalScoreBadSalaryAndCreditHistoryCriticOverdue() {
        String clientId = "client-1";
        List<Integer> overdueDays = List.of(8);
        BigDecimal disbursement = BigDecimal.valueOf(480000);
        BigDecimal salary = BigDecimal.valueOf(75000);
        when(productEngineService.getMaxPayment(disbursement)).thenReturn(BigDecimal.valueOf(40000));
        when(productEngineService.getLoansOverdue(clientId)).thenReturn(overdueDays);
        int score = scorer.getTotalScore(clientId, disbursement, salary);
        assertEquals(-1, score);
    }

    @Test
    void testTotalScoreBadSalaryAndCreditHistoryNotCriticOverdue() {
        String clientId = "client-1";
        List<Integer> overdueDays = List.of(6);
        BigDecimal disbursement = BigDecimal.valueOf(72000);
        BigDecimal salary = BigDecimal.valueOf(15000);
        when(productEngineService.getMaxPayment(disbursement)).thenReturn(BigDecimal.valueOf(6000));
        when(productEngineService.getLoansOverdue(clientId)).thenReturn(overdueDays);
        int score = scorer.getTotalScore(clientId, disbursement, salary);
        assertEquals(0, score);
    }
}
