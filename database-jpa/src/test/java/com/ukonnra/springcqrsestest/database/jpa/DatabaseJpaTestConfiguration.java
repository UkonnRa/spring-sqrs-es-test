package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.testsuite.TestSuiteConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.context.annotation.Import;

@Import({DatabaseJpaConfiguration.class, TestSuiteConfiguration.class})
@AutoConfigureJsonTesters
public class DatabaseJpaTestConfiguration {}
