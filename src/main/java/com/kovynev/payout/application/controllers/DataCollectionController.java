package com.kovynev.payout.application.controllers;

import com.kovynev.payout.application.domain.dto.TriggerOptionsDto;
import com.kovynev.payout.application.services.DataCollectionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payouts")
@AllArgsConstructor
public class DataCollectionController {

    private final DataCollectionService dataCollectionService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("processing/trigger")
    public void triggerPayoutsProcessing(@RequestBody TriggerOptionsDto options) {
        dataCollectionService.triggerPayoutsProcessing(options.getCountryName());
    }
}
