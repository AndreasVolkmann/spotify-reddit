package me.avo.spottit.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import me.avo.spottit.controller.AutomaticAuthController
import me.avo.spottit.controller.DynamicPlaylistController
import me.avo.spottit.controller.ManualAuthController
import me.avo.spottit.controller.TokenRefreshController
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.service.*
import java.util.*

val kodein = Kodein {
    val props = Properties().apply {
        load(Dependency::class.java.classLoader.getResourceAsStream("application.properties"))
    }

    fun getProperty(key: String) = props.getProperty(key)
    fun getEnvOrProp(key: String) = System.getenv(key) ?: getProperty(key)

    bind<DynamicPlaylistController>() with singleton {
        DynamicPlaylistController(
            refreshController = instance(),
            spotifyService = instance(),
            redditCredentials = instance()
        )
    }

    bind<ManualAuthController>() with singleton {
        ManualAuthController(spotifyAuthService = instance())
    }

    bind<AutomaticAuthController>() with singleton {
        AutomaticAuthController(
            spotifyAuthService = instance(),
            client = instance()
        )
    }

    bind<TokenRefreshController>() with singleton {
        TokenRefreshController(spotifyAuthService = instance())
    }

    bind<SpotifyService>() with singleton {
        SpotifyServiceImpl(authService = instance())
    }

    bind<SpotifyAuthService>() with singleton {
        SpotifyAuthServiceImpl(
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

    bind<SpotifyHeadlessAuthService>() with singleton {
        SpotifyHeadlessAuthService(serviceUrl = getEnvOrProp("SERVICE_URL"))
    }

}

object Dependency
