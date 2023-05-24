package dev.blitzcraft.blitzcontainers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.lifecycle.Startable

internal abstract class BlitzContainer<out ANNOTATION: Annotation, out CONTAINER: GenericContainer<*>>(annotation: ANNOTATION):
    Startable {
  protected val container by lazy { doCreateContainer(annotation) }
  val key by lazy { generateKey(annotation) }

  abstract fun springProperties(): Map<String, Any>
  abstract fun prepareForTest(annotation: @UnsafeVariance ANNOTATION)
  protected abstract fun generateKey(annotation: @UnsafeVariance ANNOTATION): String
  protected abstract fun createContainer(annotation: @UnsafeVariance ANNOTATION): CONTAINER

  override fun start() {
    container.start()
  }

  override fun stop() {
    container.stop()
  }

  private fun doCreateContainer(annotation: ANNOTATION) =
    createContainer(annotation).apply {
      withLabels(mapOf("dev.blitzcraft.blitzcontainers" to "true"))
    }
}