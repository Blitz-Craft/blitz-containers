package dev.blitzcraft.blitzcontainers.pubsub

import dev.blitzcraft.blitzcontainers.BlitzContainerFactory

internal class PubSubBlitzContainerFactory: BlitzContainerFactory<PubSub, PubSubBlitzContainer> {
  override fun getContainer(annotation: PubSub) = PubSubBlitzContainer(annotation)
}