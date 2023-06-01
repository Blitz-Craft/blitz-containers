package dev.blitzcraft.blitzcontainers.pubsub

import dev.blitzcraft.blitzcontainers.BlitzContainer
import org.testcontainers.containers.PubSubEmulatorContainer
import org.testcontainers.utility.DockerImageName

internal class PubSubBlitzContainer(annotation: PubSub): BlitzContainer<PubSub, PubSubEmulatorContainer>(annotation) {

  override fun generateKey(annotation: PubSub) = "${annotation.annotationClass.java.simpleName}/${annotation.tag}"

  override fun createContainer(annotation: PubSub) =
    PubSubEmulatorContainer(DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk").withTag(annotation.tag))

  override fun generateSpringProperties(annotation: PubSub) =
    mapOf(
      "spring.cloud.gcp.pubsub.emulator-host" to container.emulatorEndpoint,
      "spring.cloud.gcp.pubsub.project-id" to "blitzcontainers-pubsub"
    )

  override fun prepareForTest(annotation: PubSub) {
    val topicsAndSubscriptions = annotation.topics.associate { it.name to it.subscriptions.toSet() }

    PubSubAdminClient(container.emulatorEndpoint, "blitzcontainers-pubsub").use {
      topicsAndSubscriptions.keys.subtract(it.topics()).forEach { topic -> it.createTopic(topic) }
      topicsAndSubscriptions.forEach { (topic, subs) ->
        it.createSubscriptions(topic, subs.map { it.name }.subtract(it.subscriptions(topic)))
      }
    }
  }
}