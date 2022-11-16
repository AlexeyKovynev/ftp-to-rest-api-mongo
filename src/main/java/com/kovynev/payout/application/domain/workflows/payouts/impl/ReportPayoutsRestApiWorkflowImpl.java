package com.kovynev.payout.application.domain.workflows.payouts.impl;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import com.kovynev.payout.application.domain.db.repositories.ImportedFileRepository;
import com.kovynev.payout.application.domain.dto.PayoutDto;
import com.kovynev.payout.application.domain.enums.PaymentStatus;
import com.kovynev.payout.application.domain.external.intrum.IntrumRestClient;
import com.kovynev.payout.application.domain.workflows.payouts.ReportPayoutsRestApiWorkflow;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReportPayoutsRestApiWorkflowImpl implements ReportPayoutsRestApiWorkflow {

    private final IntrumRestClient restClient;

    private final ImportedFileRepository fileRepository;

    @Value("${include.pending.payouts:false}")
    private boolean includePendingPayouts;

    @Override
    @Transactional
    public void exportPayouts(List<ImportedFile> importedFiles) {
        fileRepository.saveAll(importedFiles);
        importedFiles.forEach(file -> {
            int failed = 0;
            for (val payout : file.getPayouts()) {
                if (!includePendingPayouts && payout.getPaymentStatus().equals(PaymentStatus.PENDING)) {
                    continue;
                }

                val reported = restClient.postPayoutRetryable(new PayoutDto(payout));
                log.info("Payout '{}'. Reported: {}", payout, reported);
                if (!reported) failed++;
                payout.setIsReported(reported);
            }
            // If all payouts were reported – mark whole file as reported
            file.setIsReported(failed == 0);
            fileRepository.save(file);
        });
    }


}
