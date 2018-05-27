package me.avo.spottit.service

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.call.receive
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.config
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.experimental.runBlocking
import me.avo.spottit.model.SpotifyCredentials
import org.slf4j.LoggerFactory
import java.net.URL

class SpotifyHeadlessAuthService(private val serviceUrl: String) {

    fun getAuthCode(spotifyCredentials: SpotifyCredentials): String {
        logger.info("Obtaining auth code from $serviceUrl")
        val code = runClient(serviceUrl, spotifyCredentials)
        when {
            code.isBlank() -> throw Exception("The received auth code is blank")
            code.length != 304 -> logger.warn("The received auth code has an invalid length ${code.length}, expected be 304")
        }
        return code
    }

    private fun runClient(url: String, spotifyCredentials: SpotifyCredentials): String = runBlocking {
        val client = HttpClient(Apache.config {
            socketTimeout = 100_000
        }) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }

        val call = client.call {
            url(URL(url))
            contentType(ContentType.Application.Json)
            method = HttpMethod.Post
            body = spotifyCredentials
        }

        val status = call.response.status
        when (status) {
            HttpStatusCode.OK -> call.receive<String>()
            else -> throw Exception("There was a problem obtaining the Spotify authorization code. Http Status: $status")
        }
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}