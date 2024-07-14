rootProject.name = "spring-sqrs-es-test"

file(".").listFiles { it ->
    it.isDirectory && it.list()?.contains("build.gradle.kts") ?: false
}?.forEach {
    println("Dir: ${it.name}")
    include(it.name)
}