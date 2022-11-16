package com.kovynev.payout.application.domain.external.intrum;

import com.kovynev.payout.application.domain.dto.PayoutDto;

public interface IntrumRestClient {
    boolean postPayoutRetryable(PayoutDto dto);
}
