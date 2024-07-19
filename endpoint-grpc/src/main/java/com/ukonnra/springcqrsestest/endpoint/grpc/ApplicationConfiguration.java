package com.ukonnra.springcqrsestest.endpoint.grpc;

import com.ukonnra.springcqrsestest.database.jpa.DatabaseJpaConfiguration;
import com.ukonnra.springcqrsestest.shared.SharedConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Import({SharedConfiguration.class, DatabaseJpaConfiguration.class})
@AllArgsConstructor
@EnableAsync
public class ApplicationConfiguration {}
