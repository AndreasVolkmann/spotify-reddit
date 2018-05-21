package me.avo.spottit.util

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.server.engine.ApplicationEngine
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.TimeUnit

fun runClient(uri: String, server: ApplicationEngine) = try {
    runBlocking {
        val response = HttpClient(Apache).use {
            it.call(uri).response
        }
        println(response.status)

        println("Request Headers")
        response.call.request.headers.entries().forEach(::println)

        println("Response Headers")
        response.headers.entries().forEach(::println)
    }
} finally {
    val timeout = 10L
    println("Shutting down server in $timeout seconds")
    server.stop(5L, timeout, TimeUnit.SECONDS)
}