package com.kovynev.payout.application.services.impl;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import com.kovynev.payout.application.domain.enums.CountryName;
import com.kovynev.payout.application.domain.workflows.payouts.ImportPayoutsWorkflowSelector;
import com.kovynev.payout.application.domain.workflows.payouts.ReportPayoutsRestApiWorkflow;
import com.kovynev.payout.application.services.DataCollectionService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class DataCollectionServiceImpl implements DataCollectionService {

    private final ImportPayoutsWorkflowSelector importWorkflowSelector;

    private final ReportPayoutsRestApiWorkflow reportPayoutsRestApiWorkflow;

    @Scheduled(cron = "${wakanda.payouts.processing.cron.schedule:-}")
    void triggerWakandaPayoutsProcessing() {
        triggerPayoutsProcessing(CountryName.WAKANDA);
    }

    @Override
    public void triggerPayoutsProcessing(CountryName countryName) {
        List<ImportedFile> todayData = importWorkflowSelector
                .getWorkflow(countryName)
                .importPayouts();
        reportPayoutsRestApiWorkflow.exportPayouts(todayData);
    }

}
