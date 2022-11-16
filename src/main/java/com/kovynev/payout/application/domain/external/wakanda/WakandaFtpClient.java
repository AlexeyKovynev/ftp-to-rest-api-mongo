package com.kovynev.payout.application.domain.external.wakanda;

import java.util.List;

public interface WakandaFtpClient {
    List<String> getListOfCsvFiles();

    List<String> downloadFileAsStrings(String filename);
}
