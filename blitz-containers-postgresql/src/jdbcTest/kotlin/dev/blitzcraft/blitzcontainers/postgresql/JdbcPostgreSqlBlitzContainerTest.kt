package dev.blitzcraft.blitzcontainers.postgresql

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("invisible_reference", "invisible_member")
// see https://youtrack.jetbrains.com/issue/KTIJ-23114/ and https://youtrack.jetbrains.com/issue/KTIJ-7662
class JdbcPostgreSqlBlitzContainerTest {

  @Test
  fun `Spring properties contains properties for connecting to PostgreSQL via jdbc`() {
    // given
    val postgreSqlBlitzContainer = PostgreSqlBlitzContainer(PostgreSql(tag = "15.3-alpine"))
    // when
    postgreSqlBlitzContainer.start()
    // then
    assertThat(postgreSqlBlitzContainer.springProperties).containsKeys("spring.datasource.url",
                                                                       "spring.datasource.username",
                                                                       "spring.datasource.password")
    assertThat(postgreSqlBlitzContainer.springProperties["spring.datasource.url"].toString()).contains("jdbc")
  }
}