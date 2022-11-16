package com.kovynev.payout.application.domain.dto;

import com.kovynev.payout.application.domain.enums.CountryName;
import lombok.Data;

@Data
public class TriggerOptionsDto {

    private CountryName countryName;
}
