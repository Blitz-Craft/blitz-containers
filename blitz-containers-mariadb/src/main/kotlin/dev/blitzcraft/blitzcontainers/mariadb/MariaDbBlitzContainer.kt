package dev.blitzcraft.blitzcontainers.mariadb

import dev.blitzcraft.blitzcontainers.BlitzContainer
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.utility.DockerImageName

internal class MariaDbBlitzContainer(annotation: MariaDb):
    BlitzContainer<MariaDb, MariaDBContainer<*>>(annotation) {

  override fun generateSpringProperties(annotation: MariaDb) =
    if (isR2dbcInClasspath()) r2dbcProperties()
    else jdbcProperties()

  private fun jdbcProperties() =
    mapOf(
      "spring.test.database.replace" to "NONE",
      "spring.datasource.url" to container.jdbcUrl,
      "spring.datasource.username" to container.username,
      "spring.datasource.password" to container.password
    )

  private fun r2dbcProperties() =
    mapOf("spring.r2dbc.url" to container.jdbcUrl.replace("jdbc", "r2dbc"),
          "spring.r2dbc.username" to container.username,
          "spring.r2dbc.password" to container.password
    )

  override fun createContainer(annotation: MariaDb) =
    CustomizedMariaDBContainer(DockerImageName.parse("mariadb").withTag(annotation.tag))

  override fun generateKey(annotation: MariaDb) =
    "${annotation.annotationClass.java.simpleName}/${annotation.tag}"

  override fun prepareForTest(annotation: MariaDb) {
    /*no op*/
  }

  private fun isR2dbcInClasspath() =
    try {
      Class.forName("org.mariadb.r2dbc.MariadbConnectionConfiguration")
      true
    } catch (e: Exception) {
      false
    }
}

