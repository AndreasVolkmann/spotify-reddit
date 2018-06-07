package me.avo.spottit.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import me.avo.spottit.controller.DynamicPlaylistController
import me.avo.spottit.controller.ManualAuthController
import me.avo.spottit.controller.TokenRefreshController
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.service.SpotifyAuthService
import me.avo.spottit.service.SpotifyAuthServiceImpl
import me.avo.spottit.service.SpotifyService
import me.avo.spottit.service.SpotifyServiceImpl
import org.slf4j.LoggerFactory
import java.util.*

val kodein = Kodein {
    val logger = LoggerFactory.getLogger(Dependency::class.java)
    val props = Properties().apply {
        try {
            load(Dependency::class.java.classLoader.getResourceAsStream("application.properties"))
        } catch (ex: NullPointerException) {
            logger.warn("Could not find application.properties")
        }
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

    bind<TokenRefreshController>() with singleton {
        TokenRefreshController(spotifyAuthService = instance())
    }

    bind<SpotifyService>() with singleton {
        SpotifyServiceImpl(authService = instance())
    }

    bind<SpotifyAuthService>() with singleton {
        SpotifyAuthServiceImpl(
            clientId = getEnvOrProp("clientId"),
            clientSecret = getEnvOrProp("clientSecret"),
            redirectUri = getEnvOrProp("redirectUri")
        )
    }

    bind<RedditCredentials>() with singleton {
        RedditCredentials(
            clientId = getEnvOrProp("reddit-clientId"),
            clientSecret = getEnvOrProp("reddit-clientSecret"),
            deviceName = getEnvOrProp("deviceName")
        )
    }

}

object Dependency
