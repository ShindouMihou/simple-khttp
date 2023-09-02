package pw.mihou.khttp
class MutableHttpResponse internal constructor(
    var body: String = "",
    var statusCode: Int = 0,
    var headers: Map<String, List<String>> = mapOf(),
    var extras: MutableMap<String, Any> = mutableMapOf()
) {
    internal val immutable get() = HttpResponse(body, statusCode, headers, extras)
}

class HttpResponse internal constructor(
    val body: String = "",
    val statusCode: Int,
    val headers: Map<String, List<String>> = mapOf(),
    val extras: Map<String, Any> = mapOf()
)