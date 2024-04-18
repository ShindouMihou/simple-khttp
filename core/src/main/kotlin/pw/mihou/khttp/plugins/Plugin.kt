package pw.mihou.khttp.plugins

import pw.mihou.khttp.MutableHttpResponse

data class Plugin(val before: PluginTask? = null, val after: PluginTask? = null)
typealias PluginTask = MutableHttpResponse.() -> Unit
