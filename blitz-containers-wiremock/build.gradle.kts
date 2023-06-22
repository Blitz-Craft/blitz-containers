dependencies {
  implementation(project(":blitz-containers-core"))
  implementation("org.testcontainers:testcontainers:${rootProject.libs.versions.testcontainers.get()}")
}