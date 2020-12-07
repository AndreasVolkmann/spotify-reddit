package me.avo.spottit.config

import me.avo.spottit.data.UpdateDetails
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.server.Server
import me.avo.spottit.service.DynamicPlaylistService
import me.avo.spottit.service.TrackFinderService
import me.avo.spottit.service.reddit.RedditServiceImpl
import me.avo.spottit.service.spotify.*
import me.avo.spottit.util.TrackFilter
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val prodKodein = Kodein {
    bind() from singleton {
        DynamicPlaylistService(
            spotifyAuthService = instance(),
            spotifyService = instance(),
            getUpdateService = factory()
        )
    }

    bind() from singleton { Server(instance()) }

    bind() from singleton {
        ManualAuthService(instance(), instance())
    }

    bind<SpotifyService>() with singleton {
        SpotifyServiceImpl(
            spotifyApi = instance(),
            getSearchAlgorithm = factory()
        )
    }

    bind<SpotifyAuthService>() with singleton {
        SpotifyAuthServiceImpl(
            clientId = Arguments.spotifyClientId,
            clientSecret = Arguments.spotifyClientSecret,
            redirectUri = Arguments.redirectUri
        )
    }

    bind() from singleton {
        RedditCredentials(
            clientId = Arguments.redditClientId,
            clientSecret = Arguments.redditClientSecret,
            deviceName = Arguments.deviceName
        )
    }

    bind<SpotifyApiService>() with singleton {
        val authService: SpotifyAuthService = kodein.direct.instance()
        SpotifyApiServiceImpl(authService.getSpotifyApi())
    }

    bind<SpotifySearchAlgorithm>() with factory { trackFilter: TrackFilter ->
        ElectronicSearchAlgorithm(instance(), trackFilter)
    }

    bind() from factory { (configuration, playlist): UpdateDetails ->
        val redditService = RedditServiceImpl(
            playlist,
            configuration.flairsToExclude,
            Arguments.redditMaxPage,
            instance()
        )
        TrackFinderService(
            playlist,
            redditService,
            TrackFilter(configuration, playlist),
            instance()
        )
    }
}
