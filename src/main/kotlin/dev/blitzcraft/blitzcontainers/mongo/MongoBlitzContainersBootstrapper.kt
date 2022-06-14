package dev.blitzcraft.blitzcontainers.mongo

import dev.blitzcraft.blitzcontainers.BlitzContainersBootstrapper
import org.springframework.test.context.TestContextAnnotationUtils.findMergedAnnotation

internal class MongoBlitzContainersBootstrapper : BlitzContainersBootstrapper {
    override fun bootAndGetProperties(testClass: Class<*>) =
        MongoContainerBootLogic.bootAndGetProperties(
            getAnnotation(testClass).version,
            getAnnotation(testClass).isNoTableScan
        )

    override fun containerKey(testClass: Class<*>): String {
        val mongoTestcontainers = getAnnotation(testClass)
        return "${Mongo::class.java.simpleName}/${mongoTestcontainers.version}/${mongoTestcontainers.isNoTableScan}"
    }

    private fun getAnnotation(testClass: Class<*>): Mongo =
        requireNotNull(findMergedAnnotation(testClass, Mongo::class.java))
}
