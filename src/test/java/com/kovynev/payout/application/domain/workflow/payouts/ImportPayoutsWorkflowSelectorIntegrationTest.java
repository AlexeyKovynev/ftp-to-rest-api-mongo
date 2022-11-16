package com.kovynev.payout.application.domain.workflow.payouts;

import com.kovynev.payout.application.domain.enums.CountryName;
import com.kovynev.payout.application.domain.workflows.payouts.ImportPayoutsWorkflowSelector;
import com.kovynev.payout.application.domain.workflows.payouts.impl.ImportPayoutsGondorWorkflowImpl;
import com.kovynev.payout.application.domain.workflows.payouts.impl.ImportPayoutsWakandaWorkflowImpl;
import com.kovynev.payout.infrastructure.PayoutIntegrationTest;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportPayoutsWorkflowSelectorIntegrationTest extends PayoutIntegrationTest {

    @Autowired
    private ImportPayoutsWorkflowSelector workflowSelector;

    @Autowired
    private ImportPayoutsGondorWorkflowImpl gondorWorkflow;

    @Autowired
    private ImportPayoutsWakandaWorkflowImpl wakandaWorkflow;

    @Test
    @DisplayName("Test Gondor import workflow selector")
    public void testGondorImportWorkflowSelector() {
        val selectedWorkflow = workflowSelector.getWorkflow(CountryName.GONDOR);
        assertTrue(selectedWorkflow instanceof ImportPayoutsGondorWorkflowImpl);
    }

    @Test
    @DisplayName("Test Wakanda import workflow selector")
    public void testWakandaImportWorkflowSelector() {
        val selectedWorkflow = workflowSelector.getWorkflow(CountryName.WAKANDA);
        assertTrue(selectedWorkflow instanceof ImportPayoutsWakandaWorkflowImpl);
    }

    @Test
    @DisplayName("Test Non-existing country import workflow selector")
    public void testNonExistingCountryImportWorkflowSelector() {
        assertThrows(IllegalArgumentException.class, () ->
                workflowSelector.getWorkflow(CountryName.valueOf("NEVERLAND")));
    }
}
