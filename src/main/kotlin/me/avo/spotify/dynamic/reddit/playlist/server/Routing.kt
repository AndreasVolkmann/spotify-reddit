package me.avo.spotify.dynamic.reddit.playlist.server

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyAuthService

fun Routing.setup(kodein: Kodein) {


    route("application") {
        class User(val name: String, val email: String)
        get("test") {
            val user = User("user name", "user@example.com")
            call.respond(FreeMarkerContent("test.ftl", mapOf("user" to user), "e"))
        }

        post("save") {
            //call.respond(FreeMarkerContent())
        }
    }

    route("spotify") {
        val spotifyAuthService: SpotifyAuthService = kodein.instance()

        get("auth/{...}") {
            val code = call.parameters["code"]
            when {
                code.isNullOrBlank() -> throw StatusException(HttpStatusCode.BadRequest, "Invalid code supplied")
                else -> {
                    call.respond(FreeMarkerContent("auth.ftl", null, "e"))
                    spotifyAuthService.grantAccess(code)
                }
            }
        }

        get("update") {
            spotifyAuthService.grantAccess(null)
            call.respond(HttpStatusCode.OK)
        }
    }

}