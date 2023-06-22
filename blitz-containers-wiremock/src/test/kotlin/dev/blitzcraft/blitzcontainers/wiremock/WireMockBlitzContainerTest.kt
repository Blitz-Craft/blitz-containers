package dev.blitzcraft.blitzcontainers.wiremock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WireMockBlitzContainerTest {
  @Test
  fun `Spring properties contain key for connecting to mocked Server`() {
    // given
    val wireMockContainer = WireMockBlitzContainer(WireMock(tag = "2.32.0-alpine",
                                                            serverUrlProperty =  "my.server.url",
                                                            stubsLocation = "wiremock"))
    // when
    wireMockContainer.start()
    // then
    assertThat(wireMockContainer.springProperties)
      .containsKeys("my.server.url")
      .extractingByKeys("my.server.url").isNotNull()
  }
}