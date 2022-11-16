package com.kovynev.payout.application.controllers;

import com.kovynev.payout.application.domain.dto.TriggerOptionsDto;
import com.kovynev.payout.application.domain.enums.CountryName;
import com.kovynev.payout.infrastructure.PayoutE2ETest;
import com.kovynev.payout.infrastructure.exceptions.BadRequestException;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataCollectionControllerE2ETest extends PayoutE2ETest {

    @Test
    @DisplayName("Test Gondor import trigger")
    public void testGondorImportTrigger() throws Exception {
        val body = new TriggerOptionsDto();
        body.setCountryName(CountryName.GONDOR);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/payouts/processing/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("UNSUPPORTED_ACTION",
                        ((BadRequestException) result.getResolvedException()).getMessageCode()));

    }

    @Test
    @DisplayName("Test Wakanda import trigger")
    public void testWakandaImportTrigger() throws Exception {
        val body = new TriggerOptionsDto();
        body.setCountryName(CountryName.WAKANDA);

        // No files in DB
        assertEquals(0, repository.findAll().size());

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/payouts/processing/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        // New files imported
        assertNotEquals(0, repository.findAll().size());
    }
}
