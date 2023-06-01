package dev.blitzcraft.blitzcontainers.postgresql

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("invisible_reference", "invisible_member")
// see https://youtrack.jetbrains.com/issue/KTIJ-23114/ and https://youtrack.jetbrains.com/issue/KTIJ-7662
class R2dbcPostgreSqlBlitzContainerTest {

  @Test
  fun `Spring properties contains properties for connecting to PostgreSQL via r2dbc`() {
    // given
    val postgreSqlBlitzContainer = PostgreSqlBlitzContainer(PostgreSql(tag = "15.3-alpine"))
    // when
    postgreSqlBlitzContainer.start()
    // then
    assertThat(postgreSqlBlitzContainer.springProperties).containsKeys("spring.r2dbc.url",
                                                                       "spring.r2dbc.username",
                                                                       "spring.r2dbc.password")
    assertThat(postgreSqlBlitzContainer.springProperties["spring.r2dbc.url"].toString()).contains("r2dbc")
  }
}