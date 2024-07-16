plugins {
  `java-library`
}

dependencies {
  implementation(project(":shared"))

  api("org.springframework.boot:spring-boot-starter-data-jpa")

  testImplementation("com.h2database:h2")
}
