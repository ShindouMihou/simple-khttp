package pw.mihou.khttp

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

class MutableHttpResponse internal constructor(
    var bodyStream: InputStream? = null,
    var body: String = "",
    var statusCode: Int = 0,
    var headers: Map<String, List<String>> = mapOf(),
    var extras: MutableMap<String, Any> = mutableMapOf(),
) {
    internal val immutable get() = HttpResponse(bodyStream, body, statusCode, headers, extras)
}

class HttpResponse internal constructor(
    val bodyStream: InputStream? = null,
    val body: String = "",
    val statusCode: Int,
    val headers: Map<String, List<String>> = mapOf(),
    val extras: Map<String, Any> = mapOf(),
) {
    /**
     * Deserializes the [body] or [bodyStream] into [T] using the [deserializer] or the default
     * [Json] deserializer provided.
     *
     * @param deserializer the deserializer to use.
     * @return [T] that is deserialized from either [body] or [bodyStream].
     */
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> json(deserializer: Json = Json): T? =
        if (bodyStream != null) {
            deserializer.decodeFromStream<T>(bodyStream)
        } else {
            deserializer
                .decodeFromString<T>(body)
        }

    /**
     * Gets the header's first value, if there are multiple values. This is useful for scenarios where
     * multiple header values are not needed.
     *
     * @param key the header's name or key.
     * @return the first value of the header, if any.
     */
    fun header(key: String): String? = headers.getOrDefault(key, null)?.firstOrNull()
}
