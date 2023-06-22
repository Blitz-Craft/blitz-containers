dependencies {
    implementation("org.testcontainers:junit-jupiter:${rootProject.libs.versions.testcontainers.get()}")

    testImplementation(project(":blitz-containers-mongo"))
    testImplementation(project(":blitz-containers-pubsub"))
}