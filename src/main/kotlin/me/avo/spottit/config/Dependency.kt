package me.avo.spottit.config

import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.service.*
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val prodKodein = Kodein {
    bind<DynamicPlaylistService>() with singleton {
        DynamicPlaylistService(
            refreshService = instance(),
            spotifyService = instance(),
            redditCredentials = instance()
        )
    }

    bind<ManualAuthService>() with singleton {
        ManualAuthService(instance(), kodein)
    }

    bind<TokenRefreshService>() with singleton {
        TokenRefreshService(spotifyAuthService = instance())
    }

    bind<SpotifyService>() with singleton {
        SpotifyServiceImpl(authService = instance())
    }

    bind<SpotifyAuthService>() with singleton {
        SpotifyAuthServiceImpl(
            clientId = Arguments.spotifyClientId,
            clientSecret = Arguments.spotifyClientSecret,
            redirectUri = Arguments.redirectUri
        )
    }

    bind<RedditCredentials>() with singleton {
        RedditCredentials(
            clientId = Arguments.redditClientId,
            clientSecret = Arguments.redditClientSecret,
            deviceName = Arguments.deviceName
        )
    }
}
