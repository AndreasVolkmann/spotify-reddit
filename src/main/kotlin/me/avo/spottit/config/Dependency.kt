package me.avo.spottit.config

import me.avo.spottit.service.DynamicPlaylistService
import me.avo.spottit.service.ManualAuthService
import me.avo.spottit.service.TokenRefreshService
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.service.SpotifyAuthService
import me.avo.spottit.service.SpotifyAuthServiceImpl
import me.avo.spottit.service.SpotifyService
import me.avo.spottit.service.SpotifyServiceImpl
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.slf4j.LoggerFactory
import java.util.*

val prodKodein = Kodein {
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

    bind<DynamicPlaylistService>() with singleton {
        DynamicPlaylistService(
            refreshService = instance(),
            spotifyService = instance(),
            redditCredentials = instance()
        )
    }

    bind<ManualAuthService>() with singleton {
        ManualAuthService(spotifyAuthService = instance())
    }

    bind<TokenRefreshService>() with singleton {
        TokenRefreshService(spotifyAuthService = instance())
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
