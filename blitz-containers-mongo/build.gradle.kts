dependencies {
  implementation(project(":blitz-containers-core"))
  implementation("org.testcontainers:mongodb:${rootProject.libs.versions.testcontainers.get()}")

  testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
}