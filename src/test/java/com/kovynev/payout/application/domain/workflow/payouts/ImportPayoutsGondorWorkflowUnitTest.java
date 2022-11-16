package com.kovynev.payout.application.domain.workflow.payouts;

import com.kovynev.payout.application.domain.workflows.payouts.ImportPayoutsWorkflow;
import com.kovynev.payout.application.domain.workflows.payouts.impl.ImportPayoutsGondorWorkflowImpl;
import com.kovynev.payout.infrastructure.PayoutUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kovynev.payout.application.domain.enums.CountryName.GONDOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImportPayoutsGondorWorkflowUnitTest extends PayoutUnitTest {

    private final ImportPayoutsWorkflow workflow = new ImportPayoutsGondorWorkflowImpl();

    @Test
    @DisplayName("Test ImportPayoutsWakandaWorkflow - supportedWorkflowName - GONDOR")
    public void testSupportedCountryNameIsGONDOR() {
        assertEquals(GONDOR, workflow.supportedWorkflowName());
    }

}
