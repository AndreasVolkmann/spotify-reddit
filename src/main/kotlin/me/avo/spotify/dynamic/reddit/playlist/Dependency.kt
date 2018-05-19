package me.avo.spotify.dynamic.reddit.playlist

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import me.avo.spotify.dynamic.reddit.playlist.service.RedditService
import me.avo.spotify.dynamic.reddit.playlist.service.SpotifyService
import java.util.*

val kodein = Kodein {
    val props = Properties().apply {
        load(Dependency::class.java.classLoader.getResourceAsStream("application.properties"))
    }

    fun getProperty(key: String) = props.getProperty(key)

    bind<SpotifyService>() with singleton {
        SpotifyService(
            clientId = getProperty("clientId"),
            clientSecret = getProperty("clientSecret"),
            redirectUri = getProperty("redirectUri"),
            userId = getProperty("userId"),
            playlistId = getProperty("playlistId")
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
