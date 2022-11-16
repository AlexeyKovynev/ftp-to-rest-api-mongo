package com.kovynev.payout.application.domain.workflow.payouts;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import com.kovynev.payout.application.domain.db.entities.Payout;
import com.kovynev.payout.application.domain.db.repositories.ImportedFileRepository;
import com.kovynev.payout.application.domain.external.wakanda.WakandaFtpClient;
import com.kovynev.payout.application.domain.workflows.payouts.impl.ImportPayoutsWakandaWorkflowImpl;
import com.kovynev.payout.infrastructure.PayoutUnitTest;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.kovynev.payout.application.domain.enums.CountryName.WAKANDA;
import static com.kovynev.payout.application.domain.enums.PaymentStatus.PAID;
import static com.kovynev.payout.application.domain.enums.PaymentStatus.PENDING;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class ImportPayoutsWakandaWorkflowUnitTest extends PayoutUnitTest {

    public static final String IRON_SUITES = "Iron suites";
    public static final String TAX_ID = "156-5562415";
    public static final String DATE = "2023-11-17";

    @InjectMocks
    private ImportPayoutsWakandaWorkflowImpl workflow;

    @Mock
    private WakandaFtpClient ftpClient;

    @Mock
    private ImportedFileRepository repository;

    @Test
    @DisplayName("Test ImportPayoutsWakandaWorkflow - importPayouts")
    public void testDataCollectionServiceTriggerPayoutsProcessing() {
        val fileName1 = "WK_payouts_" + now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_hhmms1.csv";
        val fileName2 = "WK_payouts_" + now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_hhmms2.csv";
        when(ftpClient.getListOfCsvFiles()).thenReturn(asList(fileName1, fileName2));

        val payout = new Payout("Best", "1234", now(), BigDecimal.valueOf(7123), PENDING);
        val file = new ImportedFile(WAKANDA, fileName2, true, singletonList(payout));
        when(repository.findAllBySourceFileNameIn(anyList())).thenReturn(singletonList(file));

        val lines = Arrays.asList(
                "\"Company name\";\"Company tax number\";\"Status\";\"Payment Date\";\"Amount\";",
                "\"" + IRON_SUITES + "\";\"" + TAX_ID + "\";\"" + PAID + "\";\"" + DATE + "\";\"7000\";");
        when(ftpClient.downloadFileAsStrings(fileName1)).thenReturn(lines);

        val res = workflow.importPayouts();

        Mockito.verify(ftpClient, Mockito.times(1)).getListOfCsvFiles();
        Mockito.verify(repository, Mockito.times(1)).findAllBySourceFileNameIn(anyList());
        Mockito.verify(ftpClient, Mockito.times(1)).downloadFileAsStrings(fileName1);

        val payoutRes = new Payout(IRON_SUITES, TAX_ID,
                LocalDate.parse(DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                BigDecimal.valueOf(7000), PAID);
        val fileRes = new ImportedFile(WAKANDA, fileName1, null, singletonList(payoutRes));
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(fileRes.getSourceFileName(), res.get(0).getSourceFileName());
        assertEquals(fileRes.getCountryName(), res.get(0).getCountryName());
        assertNull(fileRes.getIsReported());
        assertNull(res.get(0).getIsReported());

        assertEquals(fileRes.getPayouts().get(0).getCompanyTaxId(), res.get(0).getPayouts().get(0).getCompanyTaxId());
        assertEquals(fileRes.getPayouts().get(0).getCompanyName(), res.get(0).getPayouts().get(0).getCompanyName());
        assertEquals(fileRes.getPayouts().get(0).getPaymentDate(), res.get(0).getPayouts().get(0).getPaymentDate());
        assertEquals(fileRes.getPayouts().get(0).getPaymentAmount(), res.get(0).getPayouts().get(0).getPaymentAmount());
        assertEquals(fileRes.getPayouts().get(0).getPaymentStatus(), res.get(0).getPayouts().get(0).getPaymentStatus());
    }

    @Test
    @DisplayName("Test ImportPayoutsWakandaWorkflow - supportedWorkflowName - WAKANDA")
    public void testSupportedCountryNameIsWAKANDA() {
        assertEquals(WAKANDA, workflow.supportedWorkflowName());
    }

    //TODO: A lot of tests are needed here to cover all conditions and exceptions
}
