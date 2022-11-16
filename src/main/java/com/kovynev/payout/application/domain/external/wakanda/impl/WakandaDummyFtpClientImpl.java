package com.kovynev.payout.application.domain.external.wakanda.impl;

import com.kovynev.payout.application.domain.external.wakanda.WakandaFtpClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Profile({"test", "dev"})
public class WakandaDummyFtpClientImpl implements WakandaFtpClient {

    @Override
    public List<String> getListOfCsvFiles() {
        return Collections.singletonList("WK_payouts_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_hhmmss.csv");
    }

    @Override
    public List<String> downloadFileAsStrings(String filename) {
        return Arrays.asList(
                "\"Company name\";\"Company tax number\";\"Status\";\"Payment Date\";\"Amount\";",
                "\"Iron suites\";\"156-5562415\";\"PENDING\";\"2023-11-17\";\"7000,10\";",
                "\"Shield factory\";\"557-3562662\";\"PAID\";\"2022-05-01\";\"9999\";",
                "\"Car parts\";\"988-5561635\";\"PENDING\";\"2022-01-11\";\"7000,10\";");
    }
}
