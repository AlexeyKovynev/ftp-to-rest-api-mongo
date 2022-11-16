package com.kovynev.payout.application.domain.workflows.payouts.impl;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import com.kovynev.payout.application.domain.db.entities.Payout;
import com.kovynev.payout.application.domain.db.repositories.ImportedFileRepository;
import com.kovynev.payout.application.domain.enums.CountryName;
import com.kovynev.payout.application.domain.enums.PaymentStatus;
import com.kovynev.payout.application.domain.external.wakanda.WakandaFtpClient;
import com.kovynev.payout.application.domain.workflows.payouts.ImportPayoutsWorkflow;
import de.siegmar.fastcsv.reader.NamedCsvReader;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class ImportPayoutsWakandaWorkflowImpl implements ImportPayoutsWorkflow {

    public static final String COMPANY_NAME = "Company name";
    public static final String COMPANY_TAX_NUMBER = "Company tax number";
    public static final String STATUS = "Status";
    public static final String PAYMENT_DATE = "Payment Date";
    public static final String AMOUNT = "Amount";

    private final WakandaFtpClient ftpClient;

    private final ImportedFileRepository fileRepository;

    @Override
    public List<ImportedFile> importPayouts() {
        log.info("Wakanda FTP import started");
        val files = ftpClient.getListOfCsvFiles();
        log.info("{} file(s) found on the remote server", files.size());
        val previouslyImportedToday = fileRepository.findAllBySourceFileNameIn(files);
        val todayFilesToBeDownloaded = files.stream()
                // First remove all previously imported files from list
                // (in case it's not the first time when import was triggered today)
                .filter(o -> previouslyImportedToday.stream().noneMatch(e -> e.getSourceFileName().equals(o)))
                .filter(this::isFileHasCorrectFormatAndWasCreatedToday)
                .sorted()
                .collect(Collectors.toList());

        log.info("{} new file(s) will be downloaded", todayFilesToBeDownloaded.size());
        List<ImportedFile> importResult = new ArrayList<>();
        todayFilesToBeDownloaded.forEach(file -> {
            List<String> lines = ftpClient.downloadFileAsStrings(file);
            val mappedFile = mapCsvLinesToDbEntities(lines, file);
            importResult.add(mappedFile);
            log.info("File '{}' imported successfully", file);
        });

        log.info("Wakanda FTP import finished");
        return importResult;
    }

    private ImportedFile mapCsvLinesToDbEntities(List<String> csvLines, String file) {
        val payouts = new ArrayList<Payout>();
        NamedCsvReader.builder()
                .fieldSeparator(';')
                .build(String.join("\n", csvLines))
                .forEach(row -> {
                    try {
                        payouts.add(new Payout(
                                row.getField(COMPANY_NAME),
                                row.getField(COMPANY_TAX_NUMBER),
                                LocalDate.parse(row.getField(PAYMENT_DATE), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                parseBigDecimal(row.getField(AMOUNT)),
                                PaymentStatus.valueOf(row.getField(STATUS))));
                    } catch (ParseException e) {
                        log.error("Cannot parse file '{}' field '{}' with value '{}'", file, AMOUNT, row.getField(AMOUNT));
                    } catch (IllegalArgumentException e) {
                        log.error("New Status value found in file '{}' field '{}': '{}'", file, STATUS, row.getField(STATUS));
                    }
                });
        return new ImportedFile(CountryName.WAKANDA, file, null, payouts);
    }

    private boolean isFileHasCorrectFormatAndWasCreatedToday(String filename) {
        LocalDate dateFromFile = null;
        try {
            // Remove 'WK_payouts_' at the beginning (11 chars) and '_hhmmss.csv' from the end of the filename (11 chars)
            // Should be left only YYYYMMDD
            String dateFromFilenameAsString = filename.substring(11, filename.length() - 11);
            log.info("Date from filename as String: {}", dateFromFilenameAsString);
            dateFromFile = LocalDate.parse(dateFromFilenameAsString, DateTimeFormatter.ofPattern("yyyyMMdd"));
            log.info("Date from file: {}", dateFromFile);
        } catch (IndexOutOfBoundsException | DateTimeParseException ex) {
            // Filename has wrong format and will be removed from import queue
        }
        // Let's assume that we are in the same timezone as Wakanda
        return dateFromFile != null && dateFromFile.isEqual(LocalDate.now());
    }

    private BigDecimal parseBigDecimal(String stringValueWithComma) throws ParseException {
        val number = NumberFormat.getInstance(Locale.FRANCE);

        if (number instanceof DecimalFormat) {
            val decimal = (DecimalFormat) number;
            decimal.setParseBigDecimal(true);
            return (BigDecimal) decimal.parse(stringValueWithComma);
        }
        throw new ParseException("Cannot parse BigDecimal", 0);
    }

    @Override
    public CountryName supportedWorkflowName() {
        return CountryName.WAKANDA;
    }

}
