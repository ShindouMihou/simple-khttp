package pw.mihou.khttp.plugins

import kotlin.time.TimeSource

val ElapsedTimePlugin: Plugin = Plugin(
    before = {
        extras["elapsed.start"] = TimeSource.Monotonic.markNow()
    },
    after = {
        extras["elapsed.end"] = TimeSource.Monotonic.markNow()
        val start = extras["elapsed.start"] as TimeSource.Monotonic.ValueTimeMark
        extras["elapsed.time"] = start.elapsedNow()
    }
)