package dev.blitzcraft.blitzcontainers.mongo

import dev.blitzcraft.blitzcontainers.BlitzContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

internal class MongoBlitzContainer(annotation: Mongo): BlitzContainer<Mongo, MongoDBContainer>(annotation) {

  override fun key() = "${annotation.annotationClass.java.simpleName}/${annotation.tag}/${annotation.isNoTableScan}"

  override fun springProperties() = mapOf("spring.data.mongodb.uri" to container.replicaSetUrl)

  override fun createContainer(): MongoDBContainer {
    val container = MongoDBContainer(DockerImageName.parse("mongo").withTag(annotation.tag))
    if (annotation.isNoTableScan) {
      container.withCommand("--setParameter", "notablescan=1")
    }
    return container
  }
}