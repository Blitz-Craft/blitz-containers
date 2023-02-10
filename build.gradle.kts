import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Duration

plugins {
  id("java-library")
  id("maven-publish")
  id("groovy")
  id("signing")
  id("org.springframework.boot") version "2.7.3"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("com.adarshr.test-logger") version "3.2.0"
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  kotlin("jvm") version "1.7.0"
  kotlin("plugin.spring") version "1.7.0"
}

group = "dev.blitzcraft"
description = "The project uniting the SpringBoot tests with Testcontainers"
version = System.getenv("RELEASE_VERSION")?:"LOCAL-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val testcontainersVersion by extra("1.17.4")
val spockVersion by extra("2.3-groovy-3.0")

dependencies {
  implementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
  implementation("org.testcontainers:mongodb:$testcontainersVersion")
  implementation("org.springframework:spring-test")
  implementation("org.springframework.boot:spring-boot-test")
  implementation("org.springframework.boot:spring-boot-test-autoconfigure")
  implementation("org.testcontainers:junit-jupiter")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  testImplementation("org.spockframework:spock-core:$spockVersion")
  testImplementation("org.spockframework:spock-spring:$spockVersion")
  testImplementation("org.codehaus.groovy:groovy-all:3.0.13")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
  testImplementation("io.projectreactor:reactor-test")
}

publishing {
  publications {
    create<MavenPublication>("jars") {
      from(components["java"])
      pom {
        name.set("Blitz-Containers")
        description.set("The project uniting the SpringBoot tests with Testcontainers")
        url.set("https://blitzcraft.dev")
        licenses {
          license {
            name.set("GNU GENERAL PUBLIC LICENSE, Version 3")
            url.set("https://gnu.org/licenses/gpl-3.0.txt")
          }
        }
        developers {
          developer {
            id.set("blitz-craft")
            name.set("BlitzCraft")
            email.set("contact@blitzcraft.dev")
          }
        }
        scm {
          connection.set("https://github.com/Blitz-Craft/blitz-containers.git")
          developerConnection.set("git@github.com:Blitz-Craft/blitz-containers.git")
          url.set("https://github.com/Blitz-Craft/blitz-containers")
        }
      }
    }
  }
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
  transitionCheckOptions {
    maxRetries.set(100)
    delayBetween.set(Duration.ofSeconds(5))
  }
}

signing {
  val signingKey: String? by project
  val signingPassphrase: String? by project
  useInMemoryPgpKeys(signingKey, signingPassphrase)
  sign(publishing.publications["jars"])
}

java {
  withSourcesJar()
  withJavadocJar()
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
