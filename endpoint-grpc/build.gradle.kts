plugins {
  id("org.springframework.boot")
}

dependencies {
  implementation(project(":shared"))
  implementation("org.springframework.boot:spring-boot-starter-web")

  testImplementation(project(":test-suite"))
}
