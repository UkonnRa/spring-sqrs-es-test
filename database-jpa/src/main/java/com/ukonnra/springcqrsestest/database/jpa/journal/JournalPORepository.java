package com.ukonnra.springcqrsestest.database.jpa.journal;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JournalPORepository
    extends JpaRepository<JournalPO, UUID>, JpaSpecificationExecutor<JournalPO> {}
