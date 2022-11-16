package com.kovynev.payout.application.services;

import com.kovynev.payout.application.domain.enums.CountryName;

public interface DataCollectionService {
    void triggerPayoutsProcessing(CountryName triggerOptions);
}
