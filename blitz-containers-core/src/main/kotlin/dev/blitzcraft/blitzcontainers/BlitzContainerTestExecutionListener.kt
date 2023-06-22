package dev.blitzcraft.blitzcontainers

import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import java.lang.reflect.ParameterizedType

internal class BlitzContainerTestExecutionListener: TestExecutionListener {

  private val fixtures =
    BlitzContainersFactoriesLoader.getFactories(BlitzContainersFixture::class.java).associateBy { it.annotationClass() }

  override fun beforeTestMethod(testContext: TestContext) {
    testContext.testClass.annotations.toList()
      .filter { fixtures.containsKey(it.annotationClass.java) }
      .forEach { fixtures[it.annotationClass.java]!!.setup(it, testContext) }
  }

  override fun afterTestMethod(testContext: TestContext) {
    testContext.testClass.annotations.toList()
      .filter { fixtures.containsKey(it.annotationClass.java) }
      .forEach { fixtures[it.annotationClass.java]!!.cleanup(it, testContext) }
  }

  private fun BlitzContainersFixture<*>.annotationClass(): Class<*> =
    Class.forName((javaClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0].typeName)
}