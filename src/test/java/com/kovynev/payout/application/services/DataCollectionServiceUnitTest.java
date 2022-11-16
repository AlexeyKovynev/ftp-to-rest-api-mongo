package com.kovynev.payout.application.services;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import com.kovynev.payout.application.domain.enums.CountryName;
import com.kovynev.payout.application.domain.workflows.payouts.ImportPayoutsWorkflow;
import com.kovynev.payout.application.domain.workflows.payouts.ImportPayoutsWorkflowSelector;
import com.kovynev.payout.application.domain.workflows.payouts.ReportPayoutsRestApiWorkflow;
import com.kovynev.payout.application.services.impl.DataCollectionServiceImpl;
import com.kovynev.payout.infrastructure.PayoutUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class DataCollectionServiceUnitTest extends PayoutUnitTest {

    @InjectMocks
    private DataCollectionServiceImpl service;

    @Mock
    private ImportPayoutsWorkflowSelector importWorkflowSelector;

    @Mock
    private ImportPayoutsWorkflow importPayoutsWorkflow;

    @Mock
    private ReportPayoutsRestApiWorkflow reportPayoutsRestApiWorkflow;

    @Spy
    private List<ImportedFile> files = new ArrayList<>();

    @Test
    @DisplayName("Test DataCollectionService - triggerPayoutsProcessing")
    public void testDataCollectionServiceTriggerPayoutsProcessing() {
        when(importWorkflowSelector.getWorkflow(any(CountryName.class))).thenReturn(importPayoutsWorkflow);
        when(importPayoutsWorkflow.importPayouts()).thenReturn(files);
        doNothing().when(reportPayoutsRestApiWorkflow).exportPayouts(files);

        service.triggerPayoutsProcessing(CountryName.WAKANDA);

        Mockito.verify(importWorkflowSelector, Mockito.times(1)).getWorkflow(any(CountryName.class));
        Mockito.verify(importPayoutsWorkflow, Mockito.times(1)).importPayouts();
        Mockito.verify(reportPayoutsRestApiWorkflow, Mockito.times(1)).exportPayouts(files);

    }

}
