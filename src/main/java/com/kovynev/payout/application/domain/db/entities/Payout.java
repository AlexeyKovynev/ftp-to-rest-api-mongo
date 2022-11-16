package com.kovynev.payout.application.domain.db.entities;

import com.kovynev.payout.application.domain.enums.PaymentStatus;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@ToString
@Data
public class Payout {
    public Payout(String companyName,
                  String companyTaxId,
                  LocalDate paymentDate,
                  BigDecimal paymentAmount,
                  PaymentStatus paymentStatus) {
        this.companyName = companyName;
        this.companyTaxId = companyTaxId;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = paymentStatus;
    }

    private String companyName;
    private String companyTaxId;
    private LocalDate paymentDate;
    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus;

    private Boolean isReported;
}
