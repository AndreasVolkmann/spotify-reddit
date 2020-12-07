@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.avo.spottit.server

import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import me.avo.spottit.Spottit
import me.avo.spottit.config.Arguments
import me.avo.spottit.service.spotify.SpotifyAuthService
import org.slf4j.event.Level

class Server(
    private val spotifyAuthService: SpotifyAuthService
) {

    fun prepareServer(): ApplicationEngine = embeddedServer(
        factory = CIO, port = Arguments.port, module = module()
    )

    private fun writeCredentials(credentials: AuthorizationCodeCredentials) {
        Arguments.refreshTokenFile.writeText(credentials.refreshToken)
    }

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
                        writeCredentials(credentials)
                    }
                }
            }
        }
        install(CallLogging) { level = Level.INFO }
        install(FreeMarker, Configuration::setupFreemarker)
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
