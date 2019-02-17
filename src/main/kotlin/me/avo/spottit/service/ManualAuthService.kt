package me.avo.spottit.service

import me.avo.spottit.config.Arguments
import me.avo.spottit.config.prodKodein
import me.avo.spottit.model.Configuration
import me.avo.spottit.server.prepareServer
import me.avo.spottit.util.openUrlInBrowser
import java.util.concurrent.TimeUnit

class ManualAuthService(private val spotifyAuthService: SpotifyAuthService) : AuthorizationService {

    override fun authorize(configuration: Configuration, arguments: Arguments) {
        val scopes = getRequiredScopes(configuration)
        val uri = spotifyAuthService.getRedirectUri(scopes).toString()
        val server = prepareServer(prodKodein, arguments.port).start(wait = false)

        try {
            openUrlInBrowser(uri)
        } finally {
            Thread.sleep(5000)
            val timeout = 2L
            println("Shutting down server in $timeout seconds")
            server.stop(timeout, timeout, TimeUnit.SECONDS)
        }
    }
}
