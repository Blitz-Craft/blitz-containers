import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java-library")
  id("maven-publish")
  id("groovy")
  id("org.springframework.boot") version "2.7.3"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("com.adarshr.test-logger") version "3.2.0"
  kotlin("jvm") version "1.7.0"
  kotlin("plugin.spring") version "1.7.0"
}

group = "dev.blitzcraft"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val testcontainersVersion by extra("1.17.2")
val spockVersion by extra("2.1-groovy-3.0")

dependencies {
  implementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
  implementation("org.testcontainers:mongodb:$testcontainersVersion")
  implementation("javax.servlet:javax.servlet-api")
  implementation("org.springframework:spring-web")
  implementation("org.springframework:spring-test")
  implementation("org.springframework.boot:spring-boot-test")
  implementation("org.springframework.boot:spring-boot-test-autoconfigure")
  implementation("org.testcontainers:junit-jupiter")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  testImplementation("org.spockframework:spock-core:$spockVersion")
  testImplementation("org.spockframework:spock-spring:$spockVersion")
  testImplementation("org.codehaus.groovy:groovy-all:3.0.11")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
  testImplementation("io.projectreactor:reactor-test")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}


java {
  withSourcesJar()
}

repositories {
  mavenCentral()
}

tasks.bootJar {
  enabled = false
}

tasks.jar {
  archiveClassifier.set("")
}
tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}
tasks.withType<Test> {
  useJUnitPlatform()
}
