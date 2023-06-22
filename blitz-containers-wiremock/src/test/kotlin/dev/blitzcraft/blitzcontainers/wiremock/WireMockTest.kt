package dev.blitzcraft.blitzcontainers.wiremock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootTest
@WireMock(
  serverUrlProperty = "my.server.url",
  stubsLocation = "wiremock/myserver",
  extensionsLocation = "wiremock/extensions",
  extensionClasses = ["dev.blitzcraft.blitzcontainers.BlitzCraftHeaderInjector"]
)
class WireMockTest {

  @Autowired
  private lateinit var environment: Environment

  @Test
  fun `access to the mapped url`() {
    // given
    val serverUrl = environment["my.server.url"]!!
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder().uri(URI.create("$serverUrl/some/thing")).build()
    // when
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    // then
    assertThat(response.statusCode()).isEqualTo(200)
    assertThat(response.body()).isEqualTo("Hello world!")
  }

  @Test
  fun `access to a file`() {
    // given
    val serverUrl = environment["my.server.url"]!!
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder().uri(URI.create("$serverUrl/hello.html")).build()
    // when
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    // then
    assertThat(response.statusCode()).isEqualTo(200)
    assertThat(response.body()).contains("Hello World!")
  }

  @Test
  fun `use an extension`() {
    // given
    val serverUrl = environment["my.server.url"]!!
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder().uri(URI.create("$serverUrl/extension")).build()
    // when
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    // then
    assertThat(response.statusCode()).isEqualTo(200)
    assertThat(response.headers().firstValue("X-BlitzCraft")).hasValue("Yo !")
  }
}