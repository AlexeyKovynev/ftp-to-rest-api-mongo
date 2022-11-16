package com.kovynev.payout.application.domain.dto;

import com.kovynev.payout.application.domain.db.entities.Payout;
import lombok.Data;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Data
public class PayoutDto {
    public PayoutDto(Payout payout) {
        this.companyIdentityNumber = payout.getCompanyTaxId();
        this.paymentDate = payout.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.paymentAmount = payout.getPaymentAmount();
    }

    private String companyIdentityNumber;
    private String paymentDate;
    private BigDecimal paymentAmount;

}
