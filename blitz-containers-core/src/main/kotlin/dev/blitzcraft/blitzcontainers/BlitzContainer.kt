package dev.blitzcraft.blitzcontainers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.lifecycle.Startable

/**
 * Common Base Class for all Blitz Container
 * It associates an [Annotation] with a Testcontainer [CONTAINER] and prepare the state of this container according to
 * the properties held by the [ANNOTATION].
 * The [ANNOTATION] should hqve a tag property to define the container image tag
 */
abstract class BlitzContainer<out ANNOTATION: Annotation, out CONTAINER: GenericContainer<*>>(annotation: ANNOTATION):
    Startable {

  protected val container by lazy { doCreateContainer(annotation) }

  val key by lazy { generateKey(annotation) }
  val springProperties by lazy { generateSpringProperties(annotation) }

  /**
   * This method is called right after the container has been created but before Tests execution.
   * The goal of this method is to customize the state of the container and not to add test data.
   * See [BlitzContainersFixture] to manage test data
   * As container can be reused, this method should update the state of the container and not to replace the current state
   * by the one defined by the [ANNOTATION]
   */
  abstract fun prepareForTest(annotation: @UnsafeVariance ANNOTATION)

  /**
   * Generates Spring Properties reauired to connect to the Container based on the [annotation]
   * @param annotation [ANNOTATION], annotation associated to this container
   * @return a [Map<String,Any>] representing the Spring properties required to connect to the container
   */
  protected abstract fun generateSpringProperties(annotation: @UnsafeVariance ANNOTATION): Map<String, Any>

  /**
   * Generates a key based on the properties of the associated annotation.
   * The key should be based on the container properties (such as tag) and the type of the annotation
   * as it is used to discriminate between containers and to reuse the one with the same configuration
   * Example : "${[annotation].class}/${[annotation].tag}"
   * @param annotation ANNOTATION, annotation associated to this container
   * @return a [String] representing the container key
   */
  protected abstract fun generateKey(annotation: @UnsafeVariance ANNOTATION): String

  /**
   * Create the container for the given associated annotation
   * @param annotation [ANNOTATION] holding properties to create a container
   * @return a container
   */
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