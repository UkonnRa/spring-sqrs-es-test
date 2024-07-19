package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.database.jpa.user.UserPO;
import com.ukonnra.springcqrsestest.shared.SharedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(SharedConfiguration.class)
@EntityScan(basePackageClasses = {EventPO.class, UserPO.class})
@EnableJpaRepositories
@ComponentScan(basePackageClasses = DatabaseJpaConfiguration.class)
@EnableTransactionManagement
@Slf4j
@ImportRuntimeHints(DataJpaRuntimeHints.class)
public class DatabaseJpaConfiguration {}
