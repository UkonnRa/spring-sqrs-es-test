module springcqrsestest.database.jpa.main {
  exports com.ukonnra.springcqrsestest.database.jpa;

  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.github.spotbugs.annotations;
  requires jakarta.annotation;
  requires jakarta.persistence;
  requires jakarta.validation;
  requires liquibase.core;
  requires static lombok;
  requires org.slf4j;
  requires org.hibernate.orm.core;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.core;
  requires spring.data.commons;
  requires spring.data.jpa;
  requires spring.tx;
  requires springcqrsestest.shared.main;
}
