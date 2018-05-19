package me.avo.spotify.dynamic.reddit.playlist.server

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyService
import org.slf4j.event.Level

fun prepareServer(kodein: Kodein) = embeddedServer(Netty, 5000) {

    install(Routing) {
        setup(kodein)
    }

    install(ShutDownUrl.ApplicationCallFeature) {
        // The URL that will be intercepted
        shutDownUrl = "/application/shutdown"
        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
    }

    install(StatusPages) {
        exception<Throwable> {
            val status = when (it) {
                is StatusException -> it.status
                else -> InternalServerError
            }
            call.respond(status, "${it::class.simpleName}: ${it.message ?: "An error occurred"}")
        }
    }

    install(CallLogging) {
        level = Level.INFO
    }

}