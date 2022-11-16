package com.kovynev.payout.application.domain.workflows.payouts.impl;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import com.kovynev.payout.application.domain.enums.CountryName;
import com.kovynev.payout.application.domain.workflows.payouts.ImportPayoutsWorkflow;
import com.kovynev.payout.infrastructure.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportPayoutsGondorWorkflowImpl implements ImportPayoutsWorkflow {
    @Override
    public List<ImportedFile> importPayouts() {
        throw new BadRequestException("UNSUPPORTED_ACTION");
    }

    @Override
    public CountryName supportedWorkflowName() {
        return CountryName.GONDOR;
    }
}
