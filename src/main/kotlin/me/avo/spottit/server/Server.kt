@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.avo.spottit.server

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.StatusPages
import io.ktor.freemarker.FreeMarker
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import me.avo.spottit.Spottit
import me.avo.spottit.config.Arguments
import me.avo.spottit.service.AuthorizationService
import me.avo.spottit.service.spotify.SpotifyAuthService
import org.slf4j.event.Level

class Server(
    private val spotifyAuthService: SpotifyAuthService,
    private val authService: AuthorizationService
) {

    fun prepareServer(): ApplicationEngine = embeddedServer(
        factory = CIO, port = Arguments.port, module = module()
    )

    private fun module(): Application.() -> Unit = {
        install(Routing) {
            get("spotify/auth/{...}") {
                val code = call.parameters["code"]
                when {
                    code == null || code.isBlank() -> throw StatusException(
                        HttpStatusCode.BadRequest, "Invalid code supplied"
                    )
                    else -> {
                        val credentials = spotifyAuthService.grantAccess(code)
                        call.respond(Templates.auth(credentials.accessToken, credentials.refreshToken))
                        authService.writeCredentials(credentials)
                    }
                }
            }
        }
        install(CallLogging) { level = Level.INFO }
        install(FreeMarker) { setupFreemarker() }
        install(StatusPages) {
            exception<Throwable> {
                val status = when (it) {
                    is StatusException -> it.status
                    else -> InternalServerError
                }
                call.respond(status, "${it::class.simpleName}: ${it.message ?: "An error occurred"}")
            }
        }
    }
}

fun Configuration.setupFreemarker() {
    templateLoader = ClassTemplateLoader(Spottit::class.java.classLoader, "templates")
}
