package com.kovynev.payout.application.domain.workflows.payouts;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import com.kovynev.payout.application.domain.enums.CountryName;

import java.util.List;

public interface ImportPayoutsWorkflow {

    List<ImportedFile> importPayouts();

    CountryName supportedWorkflowName();
}
