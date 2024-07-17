package com.ukonnra.springcqrsestest.database.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventPORepository
    extends JpaRepository<EventPO, EventPO.Id>, JpaSpecificationExecutor<EventPO> {}
