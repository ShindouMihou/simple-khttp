import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertDoesNotThrow
import pw.mihou.khttp.HttpRequest
import pw.mihou.khttp.Methods
import pw.mihou.khttp.plugins.ElapsedTimePlugin
import kotlin.test.Test
import kotlin.time.Duration

class RequestTest {
    @Suppress("ktlint:standard:property-naming")
    private val PLACEHOLDER_RESPONSE =
        """
        {
          "userId": 1,
          "id": 1,
          "title": "delectus aut autem",
          "completed": false
        }
        """.trimIndent()

    @Test
    @DisplayName("Test Http Request with elapsed plugin")
    fun `test http request with elapsed plugin`() {
        runBlocking {
            val response =
                HttpRequest(
                    method = Methods.GET,
                    url = "https://jsonplaceholder.typicode.com/todos/1",
                    plugins = listOf(ElapsedTimePlugin),
                ).response()
            assertEquals(200, response.statusCode, "Response code is not status 200!")
            assertEquals(PLACEHOLDER_RESPONSE, response.body)
            assertNotNull(response.extras["elapsed.time"], "Elapsed time is null")
            assertDoesNotThrow("Elapsed time is not a duration!") {
                response.extras["elapsed.time"] as Duration
            }
        }
    }
}
