package me.avo.spotify.dynamic.reddit.playlist.config

import com.github.salomonbrys.kodein.*
import me.avo.spotify.dynamic.reddit.playlist.controller.DynamicPlaylistController
import me.avo.spotify.dynamic.reddit.playlist.model.Playlist
import me.avo.spotify.dynamic.reddit.playlist.service.RedditService
import me.avo.spotify.dynamic.reddit.playlist.service.RedditServiceImpl
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyAuthService
import java.util.*

val kodein = Kodein {
    val props = Properties().apply {
        load(Dependency::class.java.classLoader.getResourceAsStream("application.properties"))
    }

    fun getProperty(key: String) = props.getProperty(key)

    bind<DynamicPlaylistController>() with singleton {
        DynamicPlaylistController(
            spotifyAuthService = instance(),
            redditServiceFactory = factory()
        )
    }

    bind<SpotifyAuthService>() with singleton {
        SpotifyAuthService(
            clientId = getProperty("clientId"),
            clientSecret = getProperty("clientSecret"),
            redirectUri = getProperty("redirectUri")
        )
    }

    bind<RedditService>() with factory { playlist: Playlist ->
        RedditServiceImpl(
            clientId = getProperty("reddit-clientId"),
            clientSecret = getProperty("reddit-clientSecret"),
            deviceName = getProperty("deviceName"),
            playlist = playlist
        )
    }

}

object Dependency
