plugins {
  `java-library`
}

dependencies {
  implementation(project(":shared"))
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen")

  api("org.liquibase:liquibase-core")

  testImplementation(project(":test-suite"))
  testImplementation("com.h2database:h2")
}
