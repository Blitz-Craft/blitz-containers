package dev.blitzcraft.blitzcontainers.postgresql

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
@PostgreSql(tag = "15.3-alpine")
class JdbcSpringBootTest {

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