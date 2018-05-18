package me.avo.spotify.dynamic.reddit.playlist

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level

fun prepareServer(kodein: Kodein) = embeddedServer(Netty, 5000) {

    install(Routing) {

        val spotifyService: SpotifyService = kodein.instance()

        get("{...}") {
            val code = call.parameters["code"] ?: TODO("handle exception")
            when {
                code.isBlank() -> {
                    TODO("handle exception")
                    return@get
                }
                else -> call.respondText("")
            }

            spotifyService.updatePlaylist(code)

        }

        get("update") {
            spotifyService.updatePlaylist(null)
            call.respondText("Success")
        }

    }

    install(StatusPages) {
        exception<Exception> {
            call.respond(
                HttpStatusCode.InternalServerError,
                "${it::class.simpleName}: ${it.message ?: "An error occurred"}"
            )
        }
    }

    install(CallLogging) {
        level = Level.INFO
    }

}