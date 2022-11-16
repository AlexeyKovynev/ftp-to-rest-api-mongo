package com.kovynev.payout.application.domain.external.intrum.impl;

import com.kovynev.payout.application.domain.dto.PayoutDto;
import com.kovynev.payout.application.domain.external.intrum.IntrumRestClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"test", "dev"})
public class IntrumDummyRestClientImpl implements IntrumRestClient {

    @Override
    public boolean postPayoutRetryable(PayoutDto dto) {
        // Just return true instead of sending real requests somewhere
        return true;
    }
}
