dependencies {
  implementation(project(":blitz-containers-core"))
  implementation("org.testcontainers:gcloud:${rootProject.libs.versions.testcontainers.get()}")
  implementation("com.google.cloud:google-cloud-pubsub:1.123.6")

  testImplementation(platform("com.google.cloud:spring-cloud-gcp-dependencies:4.3.0"))
  testImplementation("com.google.cloud:spring-cloud-gcp-starter-pubsub")
}

tasks.test {
  maxParallelForks = 1
}