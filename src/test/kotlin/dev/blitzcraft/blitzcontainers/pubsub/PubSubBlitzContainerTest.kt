package dev.blitzcraft.blitzcontainers.pubsub

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PubSubBlitzContainerTest {

  @Test
  fun `Spring properties contains key for connecting to PubSub`() {
    // given
    val pubSubBlitzContainer = PubSubBlitzContainer(PubSub(tag = "emulators"))
    // when
    pubSubBlitzContainer.start()
    // then
    assertThat(pubSubBlitzContainer.springProperties()).containsKeys(
      "spring.cloud.gcp.pubsub.emulator-host",
      "spring.cloud.gcp.pubsub.project-id"
    )
  }
}