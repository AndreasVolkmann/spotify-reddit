package me.avo.spottit.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import me.avo.spottit.controller.DynamicPlaylistController
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.service.SpotifyAuthService
import me.avo.spottit.service.ElectronicSearchAlgorithm
import me.avo.spottit.service.SpotifyService
import me.avo.spottit.service.SpotifyServiceImpl
import java.util.*

val kodein = Kodein {
    val props = Properties().apply {
        load(Dependency::class.java.classLoader.getResourceAsStream("application.properties"))
    }

    fun getProperty(key: String) = props.getProperty(key)

    bind<DynamicPlaylistController>() with singleton {
        DynamicPlaylistController(
            spotifyAuthService = instance(),
            spotifyService = instance(),
            redditCredentials = instance()
        )
    }

    bind<SpotifyService>() with singleton {
        SpotifyServiceImpl(
            authService = instance(),
            spotifySearchAlgorithm = ElectronicSearchAlgorithm()
        )
    }

    bind<SpotifyAuthService>() with singleton {
        SpotifyAuthService(
            clientId = getProperty("clientId"),
            clientSecret = getProperty("clientSecret"),
            redirectUri = getProperty("redirectUri")
        )
    }

    bind<RedditCredentials>() with singleton {
        RedditCredentials(
            clientId = getProperty("reddit-clientId"),
            clientSecret = getProperty("reddit-clientSecret"),
            deviceName = getProperty("deviceName")
        )
    }

}

object Dependency
