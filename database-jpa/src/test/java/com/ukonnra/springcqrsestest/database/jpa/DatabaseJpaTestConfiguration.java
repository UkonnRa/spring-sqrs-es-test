package com.ukonnra.springcqrsestest.database.jpa;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import(DatabaseJpaConfiguration.class)
public class DatabaseJpaTestConfiguration {}
