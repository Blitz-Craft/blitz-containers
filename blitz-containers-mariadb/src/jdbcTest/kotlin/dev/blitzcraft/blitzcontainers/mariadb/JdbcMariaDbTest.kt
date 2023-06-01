package dev.blitzcraft.blitzcontainers.mariadb

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import javax.persistence.*


@SpringBootTest
@MariaDb(tag = "10")
@Sql("classpath:jdbc-init-script.sql")
class JdbcMariaDbTest {

  @Autowired
  lateinit var jdbcTemplate: JdbcTemplate

  @Autowired
  lateinit var personRepository: JdbcPersonRepository

  @Test
  fun `Verify MariaDb is up and running`() {
    // when
    val result = jdbcTemplate.queryForObject("SELECT 1;", String::class.java)!!
    // then
    assertThat(result).isEqualTo("1")
  }

  @Test
  fun `Save multiple Jpa Entities`() {
    // given
    val persons = listOf(Person(1, "Ilya"), Person(2, "Tof"))
    // when
    personRepository.saveAll(persons)
    // and
    val count = jdbcTemplate.queryForObject("SELECT count(*) FROM jdbc_person", Long::class.java)
    // then
    assertThat(count!!).isEqualTo(2)
  }
}

@Entity
@Table(name = "jdbc_person")
open class Person(
  @Id open val id: Long? = null,
  @Column open val name: String?
) { constructor(): this(null, null) }

interface JdbcPersonRepository: JpaRepository<Person, Long>
