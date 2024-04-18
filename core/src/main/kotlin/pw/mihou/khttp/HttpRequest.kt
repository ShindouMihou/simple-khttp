package pw.mihou.khttp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pw.mihou.khttp.plugins.Plugin
import java.io.InputStream
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Suppress("ktlint:standard:property-naming")
var Client: HttpClient = HttpClient.newHttpClient()

class HttpRequest(
    private var method: Methods,
    private var url: String,
    private var body: String? = null,
    private var parameters: Map<String, Any> = mapOf(),
    private var headers: Map<String, String> = mapOf(),
    private var options: RequestOptions.() -> Unit = {},
    private var plugins: List<Plugin> = listOf(),
    private var modifier: ((HttpRequest.Builder) -> Unit)? = null,
) {
    suspend fun response(): HttpResponse {
        return withContext(Dispatchers.IO) {
            val uri =
                if (parameters.isNotEmpty()) {
                    URI("$url?${parameters.asQueryParameters}")
                } else {
                    URI(url)
                }
            val options = RequestOptions()
            this@HttpRequest.options(options)
            val connectionBuilder =
                HttpRequest.newBuilder()
                    .uri(uri)
                    .version(options.httpVersion)
                    .method(
                        method.value,
                        if (body != null) HttpRequest.BodyPublishers.ofString(body) else HttpRequest.BodyPublishers.noBody(),
                    )
                    .timeout(options.timeout.toJavaDuration())
            headers.forEach { (key, value) -> connectionBuilder.header(key, value) }
            modifier?.let { it(connectionBuilder) }
            val mutableResponse = MutableHttpResponse()
            for (plugin in plugins) {
                plugin.before?.invoke(mutableResponse)
            }
            val connection =
                Client.send(
                    connectionBuilder.build(),
                    if (options.expectBody) {
                        if (options.expectBodyAsInputStream) {
                            java.net.http.HttpResponse.BodyHandlers.ofInputStream()
                        } else {
                            java.net.http.HttpResponse.BodyHandlers.ofString()
                        }
                    } else {
                        java.net.http.HttpResponse.BodyHandlers.discarding()
                    },
                )
            @Suppress("UNCHECKED_CAST")
            if (options.expectBody && options.expectBodyAsInputStream) {
                mutableResponse.bodyStream = (connection as java.net.http.HttpResponse<InputStream>).body()
            } else {
                mutableResponse.body =
                    if (options.expectBody) {
                        (connection as java.net.http.HttpResponse<String>).body()
                    } else {
                        ""
                    }
            }
            mutableResponse.statusCode = connection.statusCode()
            mutableResponse.headers = connection.headers().map()
            for (plugin in plugins) {
                plugin.after?.invoke(mutableResponse)
            }
            return@withContext mutableResponse.immutable
        }
    }

    suspend fun response(handler: (response: HttpResponse) -> Unit) {
        val response = response()
        handler(response)
    }

    fun copy(): pw.mihou.khttp.HttpRequest = HttpRequest(method, url, body, parameters, headers, options, plugins, modifier)

    private val Map<String, Any>.asQueryParameters: String
        get() =
            map { (key, value) -> "$key=${value.urlencoded}" }.joinToString("&")
    private val Any.urlencoded: String get() = URLEncoder.encode(toString(), Charsets.UTF_8)
}

class RequestOptions(
    var expectBody: Boolean = true,
    var expectBodyAsInputStream: Boolean = false,
    var httpVersion: HttpClient.Version = HttpClient.Version.HTTP_2,
    var timeout: Duration = 10.seconds,
)
