package me.avo.spottit.controller

import me.avo.spottit.config.kodein
import me.avo.spottit.server.prepareServer
import me.avo.spottit.service.SpotifyAuthService
import me.avo.spottit.util.openUrlInBrowser
import java.util.concurrent.TimeUnit

class ManualAuthController(private val spotifyAuthService: SpotifyAuthService) : AuthorizationController {

    override fun authorize() {
        val uri = spotifyAuthService.getRedirectUri().toString()
        val server = prepareServer(kodein).start(wait = false)

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