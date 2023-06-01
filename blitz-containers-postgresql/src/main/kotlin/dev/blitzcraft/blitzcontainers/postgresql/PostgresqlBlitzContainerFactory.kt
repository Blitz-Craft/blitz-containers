package dev.blitzcraft.blitzcontainers.postgresql

import dev.blitzcraft.blitzcontainers.BlitzContainerFactory

internal class PostgresqlBlitzContainerFactory: BlitzContainerFactory<PostgreSql, PostgreSqlBlitzContainer> {
  override fun getContainer(annotation: PostgreSql) = PostgreSqlBlitzContainer(annotation)
}