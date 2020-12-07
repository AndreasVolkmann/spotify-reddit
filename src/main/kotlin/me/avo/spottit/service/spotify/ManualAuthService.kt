package me.avo.spottit.service.spotify

import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.server.Server
import me.avo.spottit.service.AuthorizationService
import me.avo.spottit.util.openUrlInBrowser
import kotlin.concurrent.thread

class ManualAuthService(
    private val spotifyAuthService: SpotifyAuthService,
    private val server: Server
) : AuthorizationService {

    override fun authorize(configuration: Configuration) {
        thread {
            val uri = getUri(configuration)
            println("Loading ...")
            Thread.sleep(3_000)
            println("Opening Spotify auth page")
            Thread.sleep(500)
            openUrlInBrowser(uri)
        }
        println("Press ctrl+c to shut down.")
        server.prepareServer().start(wait = true)
    }

    private fun getUri(configuration: Configuration): String {
        val scopes = getRequiredScopes(configuration)
        return spotifyAuthService.getRedirectUri(scopes).toString()
    }

    override fun getRequiredScopes(configuration: Configuration): Iterable<String> {
        val private = if (configuration.playlists.any(Playlist::isPrivate)) "playlist-modify-private" else null
        val public = if (configuration.playlists.any { it.isPrivate.not() }) "playlist-modify-public" else null
        return listOfNotNull(private, public)
    }
}
