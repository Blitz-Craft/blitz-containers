package dev.blitzcraft.blitzcontainers.wiremock

import dev.blitzcraft.blitzcontainers.BlitzContainer
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.images.builder.Transferable
import org.testcontainers.utility.DockerImageName
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readBytes


internal class WireMockBlitzContainer(annotation: WireMock): BlitzContainer<WireMock, GenericContainer<*>>(annotation) {
  override fun prepareForTest(annotation: WireMock) {
    // no=op
  }

  override fun generateSpringProperties(annotation: WireMock) =
    mapOf(annotation.serverUrlProperty to "http://localhost:${container.getMappedPort(8080)}")

  override fun generateKey(annotation: WireMock) =
    "${annotation.annotationClass.java.simpleName}/${annotation.serverUrlProperty}"

  override fun createContainer(annotation: WireMock) =
    GenericContainer(DockerImageName.parse("wiremock/wiremock").withTag(annotation.tag)).apply {
      requireNotNull(annotation.stubsLocation.toPath()) { "Folder not found in the classpath: ${annotation.stubsLocation}" }
      if (annotation.extensionsLocation.isNotEmpty()) {
        requireNotNull(annotation.extensionsLocation.toPath()) { "Folder not found in the classpath: ${annotation.extensionsLocation}" }
      }

      val extensionsCommand = if (annotation.extensionClasses.isNotEmpty())
        arrayOf("--extensions", annotation.extensionClasses.joinToString(separator = ",") { it.trim() })
      else
        arrayOf()

      withExposedPorts(8080)
      withMappings(annotation.stubsLocation.toPath()!!)
      withFiles(annotation.stubsLocation.toPath()!!)
      withExtensions(annotation.extensionsLocation.toPath()!!)
      withStartupTimeout(Duration.of(180, ChronoUnit.SECONDS))
      withCommand("--verbose", *extensionsCommand)
      withLogConsumer(Slf4jLogConsumer(LoggerFactory.getLogger("WireMock")))
    }

  private fun String.toPath() =
    WireMockBlitzContainer::class.java.classLoader.getResource(this)?.toURI()?.let { Paths.get(it) }

  private fun GenericContainer<*>.withMappings(stubFolder: Path) {
    val mappings = stubFolder.resolve("mappings")
    if (mappings.exists()) {
      Files.walk(mappings)
        .filter { it.isJsonFile() }
        .forEach { withCopyToContainer(Transferable.of(it.readBytes()), "/home/wiremock/mappings/${it.fileName}") }
    }
  }

  private fun GenericContainer<*>.withFiles(stubFolder: Path) {
    val files = stubFolder.resolve("__files")
    if (files.exists()) {
      Files.walk(files)
        .filter { it.isRegularFile() }
        .forEach { withCopyToContainer(Transferable.of(it.readBytes()), "/home/wiremock/__files/${it.fileName}") }
    }
  }

  private fun Path.isJsonFile() = toFile().name.substringAfterLast(".") == "json"

  private fun GenericContainer<*>.withExtensions(extensionsPath: Path) {
    Files.walk(extensionsPath)
      .filter { it.isRegularFile() }
      .forEach {
        withCopyToContainer(Transferable.of(it.readBytes()), "/var/wiremock/extensions/${it.fileName}")
      }
  }
}
