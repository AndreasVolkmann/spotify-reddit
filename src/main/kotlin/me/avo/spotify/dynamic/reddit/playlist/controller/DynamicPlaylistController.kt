package me.avo.spotify.dynamic.reddit.playlist.controller

import me.avo.spotify.dynamic.reddit.playlist.config.kodein
import me.avo.spotify.dynamic.reddit.playlist.server.prepareServer
import me.avo.spotify.dynamic.reddit.playlist.service.RedditService
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyAuthService
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyService
import me.avo.spotify.dynamic.reddit.playlist.util.YamlConfigReader
import me.avo.spotify.dynamic.reddit.playlist.util.openUrlInBrowser
import java.io.File
import java.util.concurrent.TimeUnit

class DynamicPlaylistController(
    private val spotifyAuthService: SpotifyAuthService,
    private val redditService: RedditService
) {

    fun updateDynamicPlaylist(configPath: String) {
        val configuration = YamlConfigReader.read(File(configPath).readText())

        val uri = spotifyAuthService.getRedirectUri().toString().also(::println)

        val server = prepareServer(kodein).start(wait = false)

        try {
            openUrlInBrowser(uri)
            //runClient(uri, server)
        } finally {
            Thread.sleep(10000)
            val timeout = 5L
            println("Shutting down server in $timeout seconds")
            server.stop(5L, timeout, TimeUnit.SECONDS)
        }

        val spotifyService = SpotifyService(spotifyAuthService)
        configuration.playlists.forEach { playlist ->
            val tracks = redditService.getTracks(playlist).onEach(::println)
            //tracks.mapNotNull(RedditTrack::flair).distinct().let(::println)
            spotifyService.updatePlaylist(tracks, playlist.userId, playlist.id)
        }
    }

}