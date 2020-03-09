package me.avo.spottit.service.spotify

import me.avo.spottit.model.Configuration
import me.avo.spottit.server.Server
import me.avo.spottit.service.AuthorizationService
import me.avo.spottit.util.openUrlInBrowser

class ManualAuthService(
    private val spotifyAuthService: SpotifyAuthService,
    private val server: Server
) : AuthorizationService {

    override fun authorize(configuration: Configuration) {
        val scopes = getRequiredScopes(configuration)
        val uri = spotifyAuthService.getRedirectUri(scopes).toString()
        val server = server.prepareServer().start(wait = false)

        try {
            openUrlInBrowser(uri)
        } finally {
            Thread.sleep(5000)
            val timeout = 2000L
            println("Shutting down server in $timeout ms")
            server.stop(timeout, timeout)
        }
    }
}
