package dev.blitzcraft.blitzcontainers.mariadb

import dev.blitzcraft.blitzcontainers.BlitzContainerFactory

internal class MariaDbBlitzContainerFactory: BlitzContainerFactory<MariaDb, MariaDbBlitzContainer> {
  override fun getContainer(annotation: MariaDb) = MariaDbBlitzContainer(annotation)
}