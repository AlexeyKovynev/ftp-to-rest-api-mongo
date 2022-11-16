package com.kovynev.payout.application.domain.workflow.payouts;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import com.kovynev.payout.application.domain.db.entities.Payout;
import com.kovynev.payout.application.domain.db.repositories.ImportedFileRepository;
import com.kovynev.payout.application.domain.dto.PayoutDto;
import com.kovynev.payout.application.domain.enums.CountryName;
import com.kovynev.payout.application.domain.external.intrum.IntrumRestClient;
import com.kovynev.payout.application.domain.workflows.payouts.impl.ReportPayoutsRestApiWorkflowImpl;
import com.kovynev.payout.infrastructure.PayoutUnitTest;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static com.kovynev.payout.application.domain.enums.PaymentStatus.PAID;
import static com.kovynev.payout.application.domain.enums.PaymentStatus.PENDING;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = "include.pending.payouts=false")
public class ReportPayoutsRestApiWorkflowUnitTest extends PayoutUnitTest {

    @Autowired
    private ReportPayoutsRestApiWorkflowImpl workflow;

    @MockBean
    private IntrumRestClient restClient;

    @MockBean
    private ImportedFileRepository repository;

    @Test
    @DisplayName("Test ReportPayoutsRestApiWorkflow - exportPayouts")
    public void testReportPayoutsRestApiWorkflowExportPayouts() {
        val fileName1 = "WK_payouts_" + now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_hhmms1.csv";
        val fileName2 = "WK_payouts_" + now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_hhmms2.csv";

        val payout1 = new Payout("Best", "1234", now(), BigDecimal.valueOf(7123), PENDING);
        val payout2 = new Payout("Best2", "12345", now(), BigDecimal.valueOf(7125), PAID);
        val payout3 = new Payout("Best3", "123456", now(), BigDecimal.valueOf(7124), PAID);

        val file1 = new ImportedFile(CountryName.WAKANDA, fileName1, true, asList(payout1, payout2));
        val file2 = new ImportedFile(CountryName.WAKANDA, fileName2, true, singletonList(payout3));

        when(restClient.postPayoutRetryable(any(PayoutDto.class))).thenReturn(true).thenReturn(false);
        when(repository.save(file1)).thenReturn(file1);

        workflow.exportPayouts(asList(file1, file2));

        Mockito.verify(restClient, Mockito.times(2)).postPayoutRetryable(any(PayoutDto.class));
        Mockito.verify(repository, Mockito.times(1)).saveAll(any());
        Mockito.verify(repository, Mockito.times(2)).save(any(ImportedFile.class));
    }

}
