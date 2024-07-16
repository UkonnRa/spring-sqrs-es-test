package com.ukonnra.springcqrsestest.database.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventPORepository extends JpaRepository<EventPO, EventPO.Id> {}
