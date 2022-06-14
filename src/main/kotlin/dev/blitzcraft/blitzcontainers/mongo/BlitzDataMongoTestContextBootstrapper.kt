package dev.blitzcraft.blitzcontainers.mongo

import dev.blitzcraft.blitzcontainers.ContainersCache.bootOrReuseCompatibleContainer
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper
import org.springframework.test.context.TestContextAnnotationUtils.findMergedAnnotation

internal class BlitzDataMongoTestContextBootstrapper : SpringBootTestContextBootstrapper() {

    override fun getProperties(testClass: Class<*>): Array<String> {
        val annotation: BlitzDataMongoTest = getCustomAnnotation(testClass)
        val bootAndReturnProperties =
            { MongoContainerBootLogic.bootAndGetProperties(annotation.version, annotation.isNoTableScan) }

        return annotation.properties +
                bootOrReuseCompatibleContainer(containerKey(testClass), bootAndReturnProperties)
                    .map { "${it.key}=${it.value}" }
    }


    private fun getCustomAnnotation(testClass: Class<*>) =
        requireNotNull(findMergedAnnotation(testClass, BlitzDataMongoTest::class.java))

    private fun containerKey(testClass: Class<*>): String {
        val testcontainersDataMongoTest = getCustomAnnotation(testClass)
        return "${BlitzDataMongoTest::class.java.simpleName}/${testcontainersDataMongoTest.version}/${testcontainersDataMongoTest.isNoTableScan}"
    }
}
