package com.kovynev.payout.application.domain.db.entities;

import com.kovynev.payout.application.domain.enums.CountryName;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "importedFiles")
public class ImportedFile {
    public ImportedFile(CountryName countryName, String sourceFileName, Boolean isReported, List<Payout> payouts) {
        this.countryName = countryName;
        this.sourceFileName = sourceFileName;
        this.isReported = isReported;
        this.payouts = payouts;
        this.createdAt = LocalDateTime.now();
    }

    @Id
    private String sourceFileName;

    private CountryName countryName;

    private Boolean isReported;

    private List<Payout> payouts;

    private LocalDateTime createdAt;
}
