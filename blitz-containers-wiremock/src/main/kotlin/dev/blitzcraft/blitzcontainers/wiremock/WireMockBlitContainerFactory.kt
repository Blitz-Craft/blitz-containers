package dev.blitzcraft.blitzcontainers.wiremock

import dev.blitzcraft.blitzcontainers.BlitzContainerFactory

internal class WireMockBlitContainerFactory: BlitzContainerFactory<WireMock, WireMockBlitzContainer> {
  override fun getContainer(annotation: WireMock) = WireMockBlitzContainer(annotation)
}