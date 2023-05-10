package dev.blitzcraft.blitzcontainers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.lifecycle.Startable

internal abstract class BlitzContainer<ANNOTATION: Annotation, CONTAINER: GenericContainer<*>>(val annotation: ANNOTATION):
    Startable {
  internal val container by lazy { doCreateContainer() }

  abstract fun key(): String
  abstract fun springProperties(): Map<String, Any>
  protected abstract fun createContainer(): CONTAINER

  override fun start() {
    container.start()
  }

  override fun stop() {
    container.stop()
  }

  private fun doCreateContainer() =
    createContainer().apply {
      withLabels(
        mapOf(
          "dev.blitzcraft.blitzcontainers" to "true",
          "dev.blitzcraft.blitzcontainers.key" to key(),
        )
      )
    }
}