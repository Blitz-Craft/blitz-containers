dependencies {
    implementation("org.testcontainers:junit-jupiter:${rootProject.libs.versions.testcontainers.get()}")
    implementation("org.reflections:reflections:0.10.2")

    testImplementation(project(":blitz-containers-mongo"))
    testImplementation(project(":blitz-containers-pubsub"))
}