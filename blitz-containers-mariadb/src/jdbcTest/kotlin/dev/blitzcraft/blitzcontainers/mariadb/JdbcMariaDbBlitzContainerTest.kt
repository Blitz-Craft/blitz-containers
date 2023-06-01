package dev.blitzcraft.blitzcontainers.mariadb

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("invisible_reference", "invisible_member")
// see https://youtrack.jetbrains.com/issue/KTIJ-23114/ and https://youtrack.jetbrains.com/issue/KTIJ-7662
class JdbcMariaDbBlitzContainerTest {

  @Test
  fun `Spring properties contains properties for connecting to MariaDB via jdbc`() {
    // given
    val mariaDbBlitzContainer = MariaDbBlitzContainer(MariaDb(tag = "10"))
    // when
    mariaDbBlitzContainer.start()
    // then
    assertThat(mariaDbBlitzContainer.springProperties).containsKeys("spring.datasource.url",
                                                                    "spring.datasource.username",
                                                                    "spring.datasource.password")
    assertThat(mariaDbBlitzContainer.springProperties["spring.datasource.url"].toString()).contains("jdbc")
  }
}