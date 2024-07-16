rootProject.name = "spring-cqrs-es-test"

file(".").listFiles { it ->
    it.isDirectory && it.list()?.contains("build.gradle.kts") ?: false
}?.forEach {
    include(it.name)
}
