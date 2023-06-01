package dev.blitzcraft.blitzcontainers.mariadb

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("invisible_reference", "invisible_member")
// see https://youtrack.jetbrains.com/issue/KTIJ-23114/ and https://youtrack.jetbrains.com/issue/KTIJ-7662
class R2DbcMariaDbBlitzContainerTest {

  @Test
  fun `Spring properties contains properties for connecting to MariaDb via r2dbc`() {
    // given
    val mariaDbBlitzContainer = MariaDbBlitzContainer(MariaDb(tag = "10"))
    // when
    mariaDbBlitzContainer.start()
    // then
    assertThat(mariaDbBlitzContainer.springProperties).containsKeys("spring.r2dbc.url",
                                                                    "spring.r2dbc.username",
                                                                    "spring.r2dbc.password")
    assertThat(mariaDbBlitzContainer.springProperties["spring.r2dbc.url"].toString()).contains("r2dbc")
  }
}