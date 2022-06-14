package dev.blitzcraft.blitzcontainers.mongo

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

internal object MongoContainerBootLogic {

    fun bootAndGetProperties(dockerImageVersion: String, isNoTableScan: Boolean): Map<String, String> {
        val container = MongoDBContainer(DockerImageName.parse("mongo").withTag(dockerImageVersion))
        if (isNoTableScan) {
            container.withCommand("--setParameter", "notablescan=1")
        }
        container.start()
        return mapOf("spring.data.mongodb.uri" to container.replicaSetUrl)
    }
}
