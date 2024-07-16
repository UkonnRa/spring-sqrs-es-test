plugins {
  `java-library`
}

dependencies {
  implementation(project(":shared"))

  api("org.springframework.data:spring-data-jpa")
}
