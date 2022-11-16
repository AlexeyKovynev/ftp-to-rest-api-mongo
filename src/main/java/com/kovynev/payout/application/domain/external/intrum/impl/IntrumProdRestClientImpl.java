package com.kovynev.payout.application.domain.external.intrum.impl;

import com.kovynev.payout.application.domain.dto.PayoutDto;
import com.kovynev.payout.application.domain.external.intrum.IntrumRestClient;
import com.kovynev.payout.infrastructure.exceptions.InternalException;
import com.kovynev.payout.infrastructure.exceptions.IntrumApiFailureException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
@Component
@Profile("prod")
@AllArgsConstructor
public class IntrumProdRestClientImpl implements IntrumRestClient {

    private final RestTemplate restTemplate;

    @Value("${debt.collection.api.protocol:https}")
    private final String protocol;

    @Value("${debt.collection.api.address:none}")
    private final String address;

    @Override
    // Retry 5 times with 2 seconds interval if IntrumApiFailureException was thrown
    @Retryable(value = IntrumApiFailureException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000))
    public boolean postPayoutRetryable(PayoutDto dto) {
        val endpoint = protocol + "://" + address;
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PayoutDto> payoutPostRequest = new HttpEntity<>(dto, headers);
        try {
            log.info("Body: {}", payoutPostRequest.getBody()); //TODO: Remove after debugging
            val res = restTemplate.exchange(new URI(endpoint), HttpMethod.POST, payoutPostRequest, Object.class);
            return res.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            if (ex instanceof HttpServerErrorException) {
                // Retry in case of Server errors (5xx)
                throw new IntrumApiFailureException("EXTERNAL_SERVICE_ERROR"); //TODO: Localization
            }
            log.error("External service returned error: {}", ex.getMessage());
            return false;
        } catch (URISyntaxException ex) {
            log.error("Cannot convert string '{}' to endpoint URI..", endpoint);
            throw new InternalException("WRONG_ENDPOINT_CONFIGURATION"); //TODO: Localization
        }
    }

    @Recover
    boolean getAllStoredFailedPayoutsAndRetryWithDelay(IntrumApiFailureException e) {
        // TODO:
        //  We can also additionally include something like the 'JobRunr' library and call here more delayed retries
        //  (in one hour/day for example) for all the files in DB that were not completely reported for cases when
        //  the remote API server was offline too long.
        //  Because the existing Retryable implementation in IntrumProdRestClientImpl is rather responsible for
        //  network failures and the server downtime may be longer than the retries existing there
        log.info("This is another layer of retry");
        return true;
    }
}
