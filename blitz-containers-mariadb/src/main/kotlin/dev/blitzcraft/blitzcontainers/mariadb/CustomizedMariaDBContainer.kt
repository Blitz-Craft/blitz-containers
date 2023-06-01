package dev.blitzcraft.blitzcontainers.mariadb

import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.time.temporal.ChronoUnit

internal class CustomizedMariaDBContainer(dockerImageName: DockerImageName):
    MariaDBContainer<CustomizedMariaDBContainer>(dockerImageName) {
      init {
        waitStrategy = LogMessageWaitStrategy()
          .withRegEx(".*mariadbd: ready for connections.*\\s")
          .withTimes(2)
          .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS))
      }

  override fun waitUntilContainerStarted() {
    getWaitStrategy().waitUntilReady(this)
  }
}