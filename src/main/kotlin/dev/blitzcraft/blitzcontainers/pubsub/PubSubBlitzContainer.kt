package dev.blitzcraft.blitzcontainers.pubsub

import dev.blitzcraft.blitzcontainers.BlitzContainer
import org.testcontainers.containers.PubSubEmulatorContainer
import org.testcontainers.utility.DockerImageName

internal class PubSubBlitzContainer(annotation: PubSub): BlitzContainer<PubSub, PubSubEmulatorContainer>(annotation) {

  override fun key() = "${annotation.annotationClass.java.simpleName}/${annotation.tag}"

  override fun springProperties() =
    mapOf(
      "spring.cloud.gcp.pubsub.emulator-host" to container.emulatorEndpoint,
      "spring.cloud.gcp.pubsub.project-id" to "blitzcontainers-pubsub"
    )

  override fun createContainer() =
    PubSubEmulatorContainer(DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk").withTag(annotation.tag))
}