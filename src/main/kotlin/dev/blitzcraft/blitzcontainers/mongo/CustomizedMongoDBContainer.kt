package dev.blitzcraft.blitzcontainers.mongo

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

internal class CustomizedMongoDBContainer(dockerImageName: DockerImageName): MongoDBContainer(dockerImageName) {

  private var commands: MutableList<String> = mutableListOf("--replSet", "docker-rs")

  override fun configure() {
    withCommand(*commands.toTypedArray())
    waitingFor(Wait.forLogMessage("(?i).*waiting for connections.*", 1))
  }

  fun withNoTableScan() = apply { commands.addAll(listOf("--setParameter", "notablescan=1")) }
}