plugins {
  `java-library`
}

dependencies {
  api("jakarta.persistence:jakarta.persistence-api")
  api("org.springframework.boot:spring-boot-starter")
  api("org.springframework.boot:spring-boot-starter-validation")
  api("org.springframework.boot:spring-boot-starter-json")
  api("org.springframework.boot:spring-boot-starter-actuator")

  api("org.springframework.data:spring-data-jpa")

  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor("org.projectlombok:lombok")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testRuntimeOnly("com.h2database:h2")
}
