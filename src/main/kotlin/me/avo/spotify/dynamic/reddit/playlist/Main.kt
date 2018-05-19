package me.avo.spotify.dynamic.reddit.playlist

import com.github.salomonbrys.kodein.instance
import me.avo.spotify.dynamic.reddit.playlist.model.RedditTrack
import me.avo.spotify.dynamic.reddit.playlist.service.RedditService
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyService
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {

    val spotifyService: SpotifyService = kodein.instance()
    val redditService: RedditService = kodein.instance()
    val tracks = redditService.getTracks().onEach(::println)

    tracks.mapNotNull(RedditTrack::flair).distinct().let(::println)
    println()

    val uri = spotifyService.getRedirectUri().toString().also(::println)

    val server = prepareServer(kodein).start(wait = false)
    try {
        //runClient(uri, server)
    } finally {
        val timeout = 10L
        println("Shutting down server in $timeout seconds")
        Thread.sleep(10000)
        server.stop(5L, timeout, TimeUnit.SECONDS)
    }
    spotifyService.updatePlaylist(tracks)

}