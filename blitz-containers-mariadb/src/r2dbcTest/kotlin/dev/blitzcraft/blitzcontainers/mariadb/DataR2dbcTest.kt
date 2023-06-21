package dev.blitzcraft.blitzcontainers.mariadb

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import reactor.test.StepVerifier

@DataR2dbcTest
@MariaDb(tag = "10")
class DataR2dbcTest {

  @Autowired
  lateinit var template: R2dbcEntityTemplate

  @Test
  fun `database is up and running`() {
    // when
    val version = template.databaseClient.sql("SELECT 1;")
      .map { row, _ -> row.get(0, String::class.java) }
      .one()
    // then
    StepVerifier.create(version)
      .assertNext { assertThat(it).isEqualTo("1") }
      .verifyComplete()
  }
}
