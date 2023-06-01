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
  implementation("org.testcontainers:mariadb:${rootProject.libs.versions.testcontainers.get()}")

  "r2dbcTestImplementation"("org.springframework.boot:spring-boot-starter-data-r2dbc")
  "r2dbcTestImplementation"("org.mariadb:r2dbc-mariadb:1.1.2")

  "jdbcTestImplementation"("org.springframework.boot:spring-boot-starter-data-jpa")
  "jdbcTestImplementation"("org.mariadb.jdbc:mariadb-java-client:3.1.4")
}

tasks.register<Test>("r2dbcTest") {
  group = "verification"
  testClassesDirs = sourceSets["r2dbcTest"].output.classesDirs
  classpath = sourceSets["r2dbcTest"].runtimeClasspath
}

tasks.register<Test>("jdbcTest") {
  group = "verification"
  testClassesDirs = sourceSets["jdbcTest"].output.classesDirs
  classpath = sourceSets["jdbcTest"].runtimeClasspath
}

tasks.named("check") { dependsOn("r2dbcTest", "jdbcTest") }