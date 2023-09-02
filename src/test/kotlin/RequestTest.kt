import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertDoesNotThrow
import pw.mihou.khttp.HttpRequest
import pw.mihou.khttp.Methods
import pw.mihou.khttp.plugins.ElapsedTimePlugin
import java.net.http.HttpClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration

val PlaceholderResponse = """
{
  "userId": 1,
  "id": 1,
  "title": "delectus aut autem",
  "completed": false
}
""".trim()

val Jsonx = Json() {
    this.ignoreUnknownKeys = true
}

class RequestTest {
    @Test
    @DisplayName("Test Http Request")
    fun `test http request`() {
        runBlocking {
            val response  = HttpRequest(
                method = Methods.GET,
                url = "https://jsonplaceholder.typicode.com/todos/1",
            ).response()
            assertEquals(200, response.statusCode, "Response code is not status 200!")
            assertEquals(PlaceholderResponse, response.body)
        }
    }

    @Test
    @DisplayName("Test Http Request without Expect Body")
    fun `test http request without expect body`() {
        runBlocking {
            val response  = HttpRequest(
                method = Methods.GET,
                url = "https://jsonplaceholder.typicode.com/todos/1",
                options = {
                    expectBody = false
                }
            ).response()
            assertEquals(200, response.statusCode, "Response code is not status 200!")
            assertEquals("", response.body)
        }
    }

    @Test
    @DisplayName("Test Http Request with body")
    fun `test http request with body`() {
        runBlocking {
            val response  = HttpRequest(
                method = Methods.POST,
                url = "https://echo-http-requests.appspot.com/echo",
                body = "hello world",
            ).response()
            assertEquals(200, response.statusCode, "Response code is not status 200!")
            assertNotEquals("", response.body)
            val echo = Jsonx.decodeFromString<EchoResponse>(response.body)
            assertEquals("hello world", echo.request.body)
        }
    }

    @Test
    @DisplayName("Test Http Request with headers")
    fun `test http request with headers`() {
        runBlocking {
            val response  = HttpRequest(
                method = Methods.POST,
                url = "https://echo-http-requests.appspot.com/echo",
                headers = mapOf("X-Client-Id" to "simple-khttp")
            ).response()
            assertEquals(200, response.statusCode, "Response code is not status 200!")
            assertNotEquals("", response.body)
            val echo = Jsonx.decodeFromString<EchoResponse>(response.body)
            assertEquals("simple-khttp", echo.request.headers["X-Client-Id"])
        }
    }

    @Test
    @DisplayName("Test Http Request with HTTP/1.1")
    fun `test http request with HTTP1-1`() {
        runBlocking {
            val response  = HttpRequest(
                method = Methods.GET,
                url = "https://echo-http-requests.appspot.com/echo",
                options = {
                    httpVersion = HttpClient.Version.HTTP_1_1
                }
            ).response()
            assertEquals(200, response.statusCode, "Response code is not status 200!")
            assertNotEquals("", response.body)
            val echo = Jsonx.decodeFromString<EchoResponse>(response.body)
            assertEquals("HTTP/1.1", echo.request.httpVersion)
        }
    }

    @Test
    @DisplayName("Test Http Request with Query Params")
    fun `test http request with query params`() {
        runBlocking {
            val response  = HttpRequest(
                method = Methods.POST,
                url = "https://echo-http-requests.appspot.com/echo",
                parameters = mapOf("hello" to "world")
            ).response()
            assertEquals(200, response.statusCode, "Response code is not status 200!")
            assertNotEquals("", response.body)
            val echo = Jsonx.decodeFromString<EchoResponse>(response.body)
            assertEquals("hello=world", echo.request.queryString)
        }
    }

    @Test
    @DisplayName("Test Http Request with different methods")
    fun `test http request with different methods`() {
        runBlocking {
            for (method in Methods.entries) {
                // The echo server doesn't support PATCH for some reason.
                if (method == Methods.PATCH) {
                    continue
                }
                val response  = HttpRequest(
                    method = method,
                    url = "https://echo-http-requests.appspot.com/echo",
                    body = if (method != Methods.GET) "hello world" else null
                ).response()
                assertEquals(200, response.statusCode, "Response code is not status 200 for ${method.value}!")
                assertNotEquals("", response.body)
                val echo = Jsonx.decodeFromString<EchoResponse>(response.body)
                assertEquals(method.value, echo.request.method)
            }
        }
    }

    @Test
    @DisplayName("Test Http Request with elapsed plugin")
    fun `test http request with elapsed plugin`() {
        runBlocking {
            val response  = HttpRequest(
                method = Methods.GET,
                url = "https://jsonplaceholder.typicode.com/todos/1",
                plugins = listOf(ElapsedTimePlugin)
            ).response()
            assertEquals(200, response.statusCode, "Response code is not status 200!")
            assertEquals(PlaceholderResponse, response.body)
            assertNotNull(response.extras["elapsed.time"], "Elapsed time is null")
            assertDoesNotThrow("Elapsed time is not a duration!") {
                response.extras["elapsed.time"] as Duration
            }
        }
    }
}