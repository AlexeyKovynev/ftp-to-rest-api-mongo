package com.kovynev.payout.application.domain.workflows.payouts;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;

import java.util.List;

public interface ReportPayoutsRestApiWorkflow {
     void exportPayouts(List<ImportedFile> payouts);
}
