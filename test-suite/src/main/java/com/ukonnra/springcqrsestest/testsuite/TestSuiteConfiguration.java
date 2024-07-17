package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.SharedConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@TestConfiguration
@ComponentScan(basePackageClasses = TestSuiteConfiguration.class)
@Import(SharedConfiguration.class)
public class TestSuiteConfiguration {}
