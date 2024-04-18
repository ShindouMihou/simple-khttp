# Simple kHttp

Kotlin doesn't have simple, straightforward and flexible HTTP libraries. There are libraries such as Fuel, but the latest 
version of Fuel, in particular, version 3 alpha, lacks everything in flexibility and couldn't even allow me to set the timeout which 
caused problems.

Simple kHttp aims to fill that little tiny gap in HTTP libraries. kHttp is an extremely flexible, small and simple Kotlin-based 
coroutine wrapper around Java's new HttpClient with an additional plugin-based system. The library aims to be as simple as possible 
while still being super flexible, such as discarding the response body, etc.

> **Note!**
> 
> Simple kHTTP uses coroutines underneath, therefore, if the project that you are building doesn't support Kotlin Coroutines, 
> then this library isn't supported for your project.

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

## Installation

To install `simple-khttp`, simply follow the steps below:

**1. Add the Jitpack Repository**

Follow the one for your build tool, if you are using Gradle, then follow the top example otherwise for Maven users, 
follow the bottom example.

```groovy
repositories {
  maven { url 'https://jitpack.io' }
}
```
```maven
<repositories>
	<repository>
	  <id>jitpack.io</id>
	  <url>https://jitpack.io</url>
	</repository>
</repositories>
```

**2. Install the core library**

To get the latest release, simply head to [`Jitpack Releases`](https://jitpack.io/#pw.mihou/simple-khttp) and 
select the `Releases` tab. You can then copy the latest `Version` with the `Get it` green button.

```groovy
dependencies {
    implementation 'pw.mihou.simple-khttp:core:<version>'
}
```

```maven
<dependency>
  <groupId>pw.mihou.simple-khttp</groupId>
  <artifactId>core</artifactId>
  <version>Tag</version>
</dependency>
```

**3. Start using the library.**

You can now use the library after installing and reloading your build tool. A quick example can be seen from the 
[`Demo`](#demo) section which shows some really simple examples.

Additionally, you can also install plugins by reading the [`Plugins`](#plugins) section.

## Plugins

kHttp's plugin system is very straightforward and each plugin can have a before and after task with a little in-memory store
that allows things such as recording elapsed time.

To install a first-party plugin, simply add it as a dependency on your project, for example:
```groovy
dependencies {
  implementation 'pw.mihou.simple-khttp:plugin-name'
}
```

Third-party plugins may be installed similarly, but with a different group, or if you want to have your plugin included 
under the same first-party group, then you can create a pull request.

**Available First Party Plugins**
- `ElapsedTimePlugin` (`pw.mihou.simple-khttp:elapsed-time-plugin`)
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

## License

As part of Qucy Studios and Shindou Mihou's Open-Source Libraries, Simple kHTTP will be MIT-licensed permanently and 
irrevocably, which means that this license shall not change. You and enterprises are free to use, redistribute and  
modify the code for any purposes, although we hold no responsibility for any harm conducted by the use of the library.