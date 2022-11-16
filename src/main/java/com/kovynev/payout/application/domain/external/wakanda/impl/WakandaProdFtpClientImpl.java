package com.kovynev.payout.application.domain.external.wakanda.impl;

import com.kovynev.payout.application.domain.external.wakanda.WakandaFtpClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
@Component
@Profile("prod")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WakandaProdFtpClientImpl implements WakandaFtpClient {

    private final FtpRemoteFileTemplate template;

    @Value("${wakanda.ftp.payouts.remote.directory}")
    private String remoteDir;

    @Override
    public List<String> getListOfCsvFiles() {
        remoteDir = template.getRemoteFileSeparator() + remoteDir;

        if (!template.exists(remoteDir)) {
            log.error("Remote directory '{}' does not exist", remoteDir);
            return Collections.emptyList();
        }

        log.info("Retrieving list of '.csv' files located in '{}' on FTP server...", remoteDir);

        return Arrays.stream(template.list(remoteDir))
                .map(ftpFile -> {
                    log.info("File: {}", ftpFile);
                    return ftpFile.getName();
                })
                .filter(it -> it.toLowerCase(Locale.ROOT).endsWith(".csv"))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> downloadFileAsStrings(String filename) {
        val filePath = remoteDir + template.getRemoteFileSeparator() + filename;
        log.info("Downloading '{}' from '{}'...", filename, remoteDir);
        return template.execute(it -> IOUtils.readLines(it.readRaw(filePath), StandardCharsets.ISO_8859_1));
    }
}
