package com.kovynev.payout.application.configuration;

import com.kovynev.payout.application.domain.external.intrum.impl.IntrumRestExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonList;

@Configuration
@Profile("prod")
@AllArgsConstructor
public class IntrumRestConfiguration {

    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Bean
    public RestTemplate restTemplate() {
        val restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new IntrumRestExceptionHandler());
        restTemplate.setMessageConverters(singletonList(mappingJackson2HttpMessageConverter));
        return restTemplate;
    }
}
