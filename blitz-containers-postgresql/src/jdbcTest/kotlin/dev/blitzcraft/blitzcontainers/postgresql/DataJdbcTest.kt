package dev.blitzcraft.blitzcontainers.postgresql

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.jdbc.core.JdbcTemplate


@DataJpaTest
@PostgreSql(tag = "15.3-alpine")
class DataJdbcTest {

  @Autowired
  lateinit var jdbcTemplate: JdbcTemplate

  @Test
  fun `database is up and running`() {
    // when
    val result = jdbcTemplate.queryForObject("SELECT 1;", String::class.java)!!
    // then
    assertThat(result).isEqualTo("1")
  }
}