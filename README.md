# Simple kHttp

Kotlin doesn't have simple, straightforward and flexible HTTP libraries. There are libraries such as Fuel, but the latest 
version of Fuel, in particular, version 3 alpha,  lacks everything in flexibility and couldn't even allow me to set the timeout which 
caused problems.

Simple kHttp aims to fill that little tiny gap in HTTP libraries. kHttp is an extremely flexible, small and simple Kotlin-based 
coroutine wrapper around Java's new HttpClient with an additional plugin-based system. The library aims to be as simple as possible 
while still being super flexible, such as discarding the response body, etc.

## Demo
```kotlin
suspend fun main() {
    val response  = HttpRequest(
        method = Methods.GET,
        url = "https://jsonplaceholder.typicode.com/todos/1",
    ).response()
}
```
```kotlin
suspend fun main() {
    val response  = HttpRequest(
        method = Methods.GET,
        url = "https://jsonplaceholder.typicode.com/todos/1",
    ).response { 
        println("Received body: ${it.body}")
    }
}
```

## Plugins

kHttp's plugin system is very straightforward and each plugin can have a before and after task with a little in-memory store
that allows things such as recording elapsed time. We also have built-in plugins:
- `ElapsedTimePlugin`
  - description: records the start and end time plus the elapsed time.
  - properties: 
    - `elapsed.time`: Duration
    - `elapsed.start`: TimeSource.Monotonic.ValueTimeMark
    - `elapsed.end`: TimeSource.Monotonic.ValueTimeMark

Example with `ElapsedTimePlugin`:
```kotlin
suspend fun main() {
    val response  = HttpRequest(
        method = Methods.GET,
        url = "https://jsonplaceholder.typicode.com/todos/1",
        plugins = listOf(ElapsedTimePlugin)
    ).response()
    println("It took ${(response.extras["elapsed.time"] as Duration).inWholeMilliseconds} milliseconds to complete request!")
}
```
