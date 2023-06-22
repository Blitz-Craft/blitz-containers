package dev.blitzcraft.blitzcontainers

import org.testcontainers.containers.GenericContainer
import java.lang.reflect.ParameterizedType

/**
 * Factory to create Blitz Containers.
 * A classpath scan is done by [BlitzContainerManager] to find all the factories
 */
interface BlitzContainerFactory<out ANNOTATION: Annotation, CONTAINER: BlitzContainer<Annotation, GenericContainer<*>>> {
  fun getContainer(annotation: @UnsafeVariance ANNOTATION): CONTAINER
}