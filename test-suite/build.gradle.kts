plugins {
  `java-library`
}

dependencies {
  api("org.springframework.boot:spring-boot-starter-test")

  implementation(project(":shared"))
}
