package com.ukonnra.springcqrsestest.shared;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableAutoConfiguration
public class SharedConfiguration {
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return Jackson2ObjectMapperBuilder::build;
  }
}
