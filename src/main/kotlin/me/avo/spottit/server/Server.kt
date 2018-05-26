package me.avo.spottit.server

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
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
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import me.avo.spottit.config.Dependency
import me.avo.spottit.service.SpotifyAuthService
import org.slf4j.event.Level

fun prepareServer(kodein: Kodein): ApplicationEngine = embeddedServer(
    Netty, 5000, module = module(kodein)
)

fun module(kodein: Kodein): Application.() -> Unit = {
    install(Routing) {
        val spotifyAuthService: SpotifyAuthService = kodein.instance()
        get("auth/{...}") {
            val code = call.parameters["code"]
            when {
                code == null || code.isBlank() -> throw StatusException(
                    HttpStatusCode.BadRequest, "Invalid code supplied"
                )
                else -> {
                    val credentials = spotifyAuthService.getAccessToken(code)
                    call.respond(Templates.auth(credentials.accessToken, credentials.refreshToken))
                }
            }
        }
    }

    install(FreeMarker) {
        setupFreemarker()
    }

    install(ShutDownUrl.ApplicationCallFeature) {
        // The URL that will be intercepted
        shutDownUrl = "/application/shutdown"
        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 }
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

