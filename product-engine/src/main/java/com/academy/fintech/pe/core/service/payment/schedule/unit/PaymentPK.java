package com.academy.fintech.pe.core.service.payment.schedule.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPK implements Serializable {
    private Long scheduleId;
    private int periodNumber;
}
