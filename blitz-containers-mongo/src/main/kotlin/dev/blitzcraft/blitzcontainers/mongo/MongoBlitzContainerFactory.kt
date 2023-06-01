package dev.blitzcraft.blitzcontainers.mongo

import dev.blitzcraft.blitzcontainers.BlitzContainerFactory

internal class MongoBlitzContainerFactory: BlitzContainerFactory<Mongo, MongoBlitzContainer> {
  override fun getContainer(annotation: Mongo) = MongoBlitzContainer(annotation)
}