package me.avo.spotify.dynamic.reddit.playlist.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import me.avo.spotify.dynamic.reddit.playlist.controller.DynamicPlaylistController
import me.avo.spotify.dynamic.reddit.playlist.service.RedditService
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyAuthService
import java.util.*

val kodein = Kodein {
    val props = Properties().apply {
        load(Dependency::class.java.classLoader.getResourceAsStream("application.properties"))
    }

    fun getProperty(key: String) = props.getProperty(key)

    bind<DynamicPlaylistController>() with singleton { DynamicPlaylistController(instance(), instance()) }

    bind<SpotifyAuthService>() with singleton {
        SpotifyAuthService(
            clientId = getProperty("clientId"),
            clientSecret = getProperty("clientSecret"),
            redirectUri = getProperty("redirectUri")
        )
    }

    bind<RedditService>() with singleton {
        RedditService(
            clientId = getProperty("reddit-clientId"),
            clientSecret = getProperty("reddit-clientSecret"),
            deviceName = getProperty("deviceName")
        )
    }

}

object Dependency
