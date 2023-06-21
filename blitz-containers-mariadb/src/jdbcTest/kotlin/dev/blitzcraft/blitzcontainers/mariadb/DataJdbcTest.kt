package dev.blitzcraft.blitzcontainers.mariadb

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.jdbc.core.JdbcTemplate


@DataJdbcTest
@MariaDb(tag = "10")
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