package dev.blitzcraft.blitzcontainers.mongo

import dev.blitzcraft.blitzcontainers.BlitzContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

internal class MongoBlitzContainer(annotation: Mongo): BlitzContainer<Mongo, MongoDBContainer>(annotation) {

  override fun generateKey(annotation: Mongo) =
    "${annotation.annotationClass.java.simpleName}/${annotation.tag}/${annotation.isNoTableScan}"

  override fun createContainer(annotation: Mongo) =
    CustomizedMongoDBContainer(DockerImageName.parse("mongo").withTag(annotation.tag)).apply {
      if (annotation.isNoTableScan) {
        withNoTableScan()
      }
    }

  override fun generateSpringProperties(annotation: Mongo) = mapOf("spring.data.mongodb.uri" to container.replicaSetUrl)

  override fun prepareForTest(annotation: Mongo) { /*no op */}
}