package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.testsuite.TestSuiteConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import({DatabaseJpaConfiguration.class, TestSuiteConfiguration.class})
public class DatabaseJpaTestConfiguration {}
