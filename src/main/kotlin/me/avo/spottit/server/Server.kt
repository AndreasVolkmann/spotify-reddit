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
import me.avo.spottit.config.Dependency
import me.avo.spottit.controller.ManualAuthController
import me.avo.spottit.service.SpotifyAuthService
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import org.slf4j.event.Level

fun prepareServer(kodein: Kodein): ApplicationEngine = embeddedServer(
    factory = CIO,
    port = System.getenv("PORT")?.toInt() ?: 5000,
    module = module(kodein)
)

fun module(kodein: Kodein): Application.() -> Unit = {
    install(Routing) {

        val spotifyAuthService: SpotifyAuthService by kodein.instance()
        val manualAuthController: ManualAuthController by kodein.instance()

        get("spotify/auth/{...}") {
            val code = call.parameters["code"]
            when {
                code == null || code.isBlank() -> throw StatusException(
                    HttpStatusCode.BadRequest, "Invalid code supplied"
                )
                else -> {
                    val credentials = spotifyAuthService.grantAccess(code)
                    call.respond(Templates.auth(credentials.accessToken, credentials.refreshToken))
                    manualAuthController.writeCredentials(credentials)
                }
            }
        }
    }

    install(FreeMarker) {
        setupFreemarker()
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

fun Configuration.setupFreemarker() {
    templateLoader = ClassTemplateLoader(Dependency::class.java.classLoader, "templates")
}

