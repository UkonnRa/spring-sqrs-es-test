plugins {
  id("org.springframework.boot")
}

dependencies {
  implementation(project(":shared"))
  implementation(project(":database-jpa"))
  implementation("org.springframework.boot:spring-boot-starter-web")

  runtimeOnly("com.h2database:h2")

  testImplementation(project(":test-suite"))
}
