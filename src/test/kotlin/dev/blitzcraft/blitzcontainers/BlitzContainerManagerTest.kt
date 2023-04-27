package dev.blitzcraft.blitzcontainers

import dev.blitzcraft.blitzcontainers.mongo.Mongo
import dev.blitzcraft.blitzcontainers.pubsub.PubSub
import dev.blitzcraft.blitzcontainers.pubsub.Topic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.DockerClientFactory

class BlitzContainerManagerTest {

  private val dockerClient = DockerClientFactory.instance().client()

  @BeforeEach
  fun setup() {
    BlitzContainerManager.clearCache()
    killAllBlitzContainers()
  }

  @AfterEach
  fun cleanup() {
    BlitzContainerManager.clearCache()
    killAllBlitzContainers()
  }

  @Test
  fun `should start multiple containers`() {
    // when
    val springProperties = BlitzContainerManager.startOrReuseContainersFor(
      Mongo(tag = "5"),
      PubSub(tag = "emulators", topics = arrayOf(Topic(name = "topic1")))
    )
    // then
    assertThat(runningBlitzContainers()).hasSize(2)
    assertThat(springProperties).isNotEmpty()
  }

  @Test
  fun `should do nothing for unmanaged annotation`() {
    // when
    val springProperties = BlitzContainerManager.startOrReuseContainersFor(Test())
    // then
    assertThat(runningBlitzContainers()).isEmpty()
    assertThat(springProperties).isEmpty()
  }

  @Test
  fun `should reuse a container`() {
    // given
    BlitzContainerManager.startOrReuseContainersFor(Mongo(tag = "5"))
    // when
    BlitzContainerManager.startOrReuseContainersFor(Mongo(tag = "5"))
    // then
    assertThat(runningBlitzContainers()).hasSize(1)
  }

  private fun runningBlitzContainers() =
    dockerClient.listContainersCmd().withLabelFilter(mapOf("dev.blitzcraft.blitzcontainers" to "true")).exec()

  private fun killAllBlitzContainers() =
    runningBlitzContainers().forEach { dockerClient.killContainerCmd(it.id).exec() }
}