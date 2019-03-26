package me.avo.spottit.config

import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.service.*
import me.avo.spottit.util.TrackFilter
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.*

val prodKodein = Kodein {
    bind<DynamicPlaylistService>() with singleton {
        DynamicPlaylistService(
            refreshService = instance(),
            spotifyService = instance(),
            getRedditService = factory2(),
            getTrackFilter = factory2()
        )
    }

    bind<ManualAuthService>() with singleton {
        ManualAuthService(instance(), kodein)
    }

    bind<TokenRefreshService>() with singleton {
        TokenRefreshService(spotifyAuthService = instance())
    }

    bind<SpotifyService>() with singleton {
        SpotifyServiceImpl(authService = instance(), getSearchAlgorithm = factory())
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

    bind<SpotifySearchAlgorithm>() with factory { trackFilter: TrackFilter ->
        val authService: SpotifyAuthService = kodein.direct.instance()
        ElectronicSearchAlgorithm(authService.getSpotifyApi(), trackFilter)
    }

    bind<RedditService>() with factory { playlist: Playlist, flairsToExclude: List<String> ->
        RedditServiceImpl(playlist, flairsToExclude, Arguments.redditMaxPage, instance())
    }

    bind<TrackFilter>() with factory { configuration: Configuration, playlist: Playlist ->
        TrackFilter(configuration, playlist)
    }
}
