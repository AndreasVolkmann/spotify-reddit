package me.avo.spotify.dynamic.reddit.playlist.controller

import me.avo.spotify.dynamic.reddit.playlist.kodein
import me.avo.spotify.dynamic.reddit.playlist.model.RedditTrack
import me.avo.spotify.dynamic.reddit.playlist.server.prepareServer
import me.avo.spotify.dynamic.reddit.playlist.service.RedditService
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyService
import me.avo.spotify.dynamic.reddit.playlist.util.openUrlInBrowser
import java.util.concurrent.TimeUnit

class DynamicPlaylistController(
    private val spotifyService: SpotifyService,
    private val redditService: RedditService
) {

    fun updateDynamicPlaylist() {
        val tracks = redditService.getTracks().onEach(::println)

        tracks.mapNotNull(RedditTrack::flair).distinct().let(::println)
        println()

        val uri = spotifyService.getRedirectUri().toString().also(::println)

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

        spotifyService.updatePlaylist(tracks)
    }

}