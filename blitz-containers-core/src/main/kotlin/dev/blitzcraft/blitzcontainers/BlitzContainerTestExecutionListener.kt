package dev.blitzcraft.blitzcontainers

import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes
import org.reflections.util.ClasspathHelper
import org.springframework.beans.BeanUtils
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import java.lang.reflect.ParameterizedType

internal class BlitzContainerTestExecutionListener: TestExecutionListener {

  @Suppress("UNCHECKED_CAST")
  private val fixtures =
    Reflections(ClasspathHelper.forJavaClassPath())
      .get(SubTypes.of(BlitzContainersFixture::class.java))
      .map { BeanUtils.instantiateClass(Class.forName(it)) as BlitzContainersFixture<Annotation> }
      .associateBy { it.annotationClass() }

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

  private fun BlitzContainersFixture<Annotation>.annotationClass(): Class<*> =
    Class.forName((javaClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0].typeName)
}