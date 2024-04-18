import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EchoResponse(
    val request: EchoedRequest,
    val status: String
)

@Serializable
data class EchoedRequest(
    val body: String,
    val created: String,
    val headers: Map<String, String>,
    @SerialName("http_version") val httpVersion: String,
    val method: String,
    @SerialName("query_string") val queryString: String,
)
