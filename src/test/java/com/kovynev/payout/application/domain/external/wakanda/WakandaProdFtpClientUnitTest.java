package com.kovynev.payout.application.domain.external.wakanda;

import com.kovynev.payout.application.domain.external.wakanda.impl.WakandaProdFtpClientImpl;
import com.kovynev.payout.infrastructure.PayoutUnitTest;
import lombok.val;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;

import java.time.format.DateTimeFormatter;

import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WakandaProdFtpClientUnitTest extends PayoutUnitTest {

    private WakandaProdFtpClientImpl ftpClient;

    @MockBean
    private FtpRemoteFileTemplate template;

    @BeforeEach
    public void init() {
        ftpClient = new WakandaProdFtpClientImpl(template, "test");
    }

    @Test
    @DisplayName("Test WakandaProdFtpClient - downloadFileAsStrings")
    public void testWakandaProdFtpClientImplDownloadFileAsStrings() {
        val line = "line";

        when(template.execute(any())).thenReturn(singletonList(line));

        assertEquals(line, ftpClient.downloadFileAsStrings("filename").get(0));

        Mockito.verify(template, Mockito.times(1)).execute(any());
    }

    @Test
    @DisplayName("Test WakandaProdFtpClient - getListOfCsvFiles")
    public void testWakandaProdFtpClientImplGetListOfCsvFiles() {
        val name = "WK_payouts_" + now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_hhmms1.csv";
        val file = new FTPFile();
        file.setName(name);
        val files = new FTPFile[1];
        files[0] = file;

        doReturn(true).when(template).exists(anyString());
        when(template.list(any())).thenReturn(files);

        assertEquals(name, ftpClient.getListOfCsvFiles().get(0));

        Mockito.verify(template, Mockito.times(1)).exists(any());
        Mockito.verify(template, Mockito.times(1)).list(any());
    }
}
