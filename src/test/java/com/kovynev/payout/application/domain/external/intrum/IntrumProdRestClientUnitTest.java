package com.kovynev.payout.application.domain.external.intrum;

import com.kovynev.payout.application.domain.db.entities.Payout;
import com.kovynev.payout.application.domain.dto.PayoutDto;
import com.kovynev.payout.application.domain.external.intrum.impl.IntrumProdRestClientImpl;
import com.kovynev.payout.infrastructure.PayoutUnitTest;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;

import static com.kovynev.payout.application.domain.enums.PaymentStatus.PENDING;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class IntrumProdRestClientUnitTest extends PayoutUnitTest {

    private IntrumProdRestClientImpl restClient;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    public void init() {
        restClient = new IntrumProdRestClientImpl(restTemplate, "http", "test.com");
    }

    @Test
    @DisplayName("Test IntrumProdRestClientImpl - postPayout - succeeded")
    public void testIntrumProdRestClientImplPostPayoutSuccess() {
        val payout = new Payout("Best", "1234", now(), BigDecimal.valueOf(7123), PENDING);

        val dto = new PayoutDto(payout);
        ResponseEntity<Object> testEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                any(URI.class), any(HttpMethod.class), any(HttpEntity.class), ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(testEntity);

        assertTrue(restClient.postPayoutRetryable(dto));

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), ArgumentMatchers.<Class<Object>>any());
    }

    @Test
    @DisplayName("Test ReportPayoutsRestApiWorkflow - exportPayouts - failed")
    public void testIntrumProdRestClientImplPostPayoutFail() {
        val payout = new Payout("Best", "1234", now(), BigDecimal.valueOf(7123), PENDING);

        val dto = new PayoutDto(payout);
        ResponseEntity<Object> testEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(
                any(URI.class), any(HttpMethod.class), any(HttpEntity.class), ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(testEntity);

        assertFalse(restClient.postPayoutRetryable(dto));

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), ArgumentMatchers.<Class<Object>>any());
    }

    //TODO: More tests needed here to cover all conditions and exceptions
}
