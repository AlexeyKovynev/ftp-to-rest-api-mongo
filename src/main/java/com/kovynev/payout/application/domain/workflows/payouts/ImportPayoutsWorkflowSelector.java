package com.kovynev.payout.application.domain.workflows.payouts;

import com.kovynev.payout.application.domain.enums.CountryName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ImportPayoutsWorkflowSelector {

    private final List<ImportPayoutsWorkflow> importPayoutsWorkflows;

    public ImportPayoutsWorkflow getWorkflow(CountryName countryName) {
        return importPayoutsWorkflows.stream()
                .filter(w -> w.supportedWorkflowName().equals(countryName))
                .findAny().orElseThrow(IllegalArgumentException::new);
    }

}
