package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.database.jpa.user.UserPO;
import com.ukonnra.springcqrsestest.shared.SharedConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({SharedConfiguration.class})
@EntityScan(basePackageClasses = {EventPO.class, UserPO.class})
@EnableJpaRepositories(basePackageClasses = EventPORepository.class)
@EnableTransactionManagement
public class DatabaseJpaConfiguration {}
