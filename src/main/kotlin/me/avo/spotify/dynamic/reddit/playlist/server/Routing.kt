package me.avo.spotify.dynamic.reddit.playlist.server

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyAuthService

fun Routing.setup(kodein: Kodein) {

    route("spotify") {
        val spotifyAuthService: SpotifyAuthService = kodein.instance()

        get("auth/{...}") {
            val code = call.parameters["code"]
            when {
                code.isNullOrBlank() -> throw StatusException(HttpStatusCode.BadRequest, "Invalid code supplied")
                else -> {
                    spotifyAuthService.grantAccess(code)
                    call.respond(Templates.auth())
                }
            }
        }

        get("update") {
            spotifyAuthService.grantAccess(null)
            call.respond(HttpStatusCode.OK)
        }
    }

}