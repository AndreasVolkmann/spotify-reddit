package me.avo.spotify.dynamic.reddit.playlist.server

import com.github.salomonbrys.kodein.Kodein
import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.StatusPages
import io.ktor.freemarker.FreeMarker
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import me.avo.spotify.dynamic.reddit.playlist.config.Dependency
import org.slf4j.event.Level

fun prepareServer(kodein: Kodein): ApplicationEngine = embeddedServer(Netty, 5000, module = main(kodein))

fun main(kodein: Kodein): Application.() -> Unit = {
    install(Routing) {
        setup(kodein)
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