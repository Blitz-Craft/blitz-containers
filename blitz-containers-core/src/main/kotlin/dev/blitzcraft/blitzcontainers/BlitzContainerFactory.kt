package dev.blitzcraft.blitzcontainers

import org.testcontainers.containers.GenericContainer

/**
 * Factory to create Blitz Container.
 * A classpath scan is done by [BlitzContainerManager] to find all the factories
 */
interface BlitzContainerFactory<ANNOTATION: Annotation, CONTAINER: BlitzContainer<Annotation, GenericContainer<*>>> {
  fun getContainer(annotation: ANNOTATION): CONTAINER
}