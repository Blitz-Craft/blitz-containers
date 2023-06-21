val r2dbcTest: SourceSet by sourceSets.creating
configurations[r2dbcTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
kotlin.target.compilations.getByName("r2dbcTest").associateWith(kotlin.target.compilations.getByName("main"))

val jdbcTest: SourceSet by sourceSets.creating
configurations[jdbcTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
kotlin.target.compilations.getByName("jdbcTest").associateWith(kotlin.target.compilations.getByName("main"))

idea {
  module {
    testSources.from(r2dbcTest.allJava.srcDirs)
    testResources.from(r2dbcTest.resources.srcDirs)
    testSources.from(jdbcTest.allJava.srcDirs)
    testResources.from(jdbcTest.resources.srcDirs)
  }
}

dependencies {
  implementation(project(":blitz-containers-core"))
  implementation("org.testcontainers:postgresql:${rootProject.libs.versions.testcontainers.get()}")

  "r2dbcTestImplementation"("org.springframework.boot:spring-boot-starter-data-r2dbc")
  "r2dbcTestImplementation"("org.postgresql:r2dbc-postgresql")

  "jdbcTestImplementation"("org.springframework.boot:spring-boot-starter-data-jpa")
  "jdbcTestImplementation"("org.springframework.boot:spring-boot-starter-data-jdbc")
  "jdbcTestImplementation"("org.postgresql:postgresql")
}

tasks.register<Test>("r2dbcTest") {
  group = "verification"
  testClassesDirs = r2dbcTest.output.classesDirs
  classpath = r2dbcTest.runtimeClasspath
}

tasks.register<Test>("jdbcTest") {
  group = "verification"
  testClassesDirs = jdbcTest.output.classesDirs
  classpath = jdbcTest.runtimeClasspath
}

tasks.named("check") { dependsOn("r2dbcTest", "jdbcTest") }