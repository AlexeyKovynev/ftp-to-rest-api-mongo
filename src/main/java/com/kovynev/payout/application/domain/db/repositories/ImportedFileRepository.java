package com.kovynev.payout.application.domain.db.repositories;

import com.kovynev.payout.application.domain.db.entities.ImportedFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ImportedFileRepository extends MongoRepository<ImportedFile, String> {
    List<ImportedFile> findAllBySourceFileNameIn(Collection<String> sourceFileName);
}
