module springcqrsestest.shared.main {
  exports com.ukonnra.springcqrsestest.shared.user;
  exports com.ukonnra.springcqrsestest.shared.journal;
  exports com.ukonnra.springcqrsestest.shared;

  requires com.fasterxml.jackson.annotation;
  requires jakarta.validation;
  requires static lombok;
  requires org.slf4j;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.core;
  requires spring.tx;
  requires spring.web;
}
