package me.avo.spottit.config

import me.avo.spottit.data.UpdateDetails
import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.server.Server
import me.avo.spottit.service.DynamicPlaylistService
import me.avo.spottit.service.reddit.RedditService
import me.avo.spottit.service.reddit.RedditServiceImpl
import me.avo.spottit.service.spotify.*
import me.avo.spottit.util.TrackFilter
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.*

val prodKodein = Kodein {
    bind<DynamicPlaylistService>() with singleton {
        DynamicPlaylistService(
            spotifyAuthService = instance(),
            getUpdateService = factory()
        )
    }

    bind<Server>() with singleton { Server(instance(), instance()) }

    bind<ManualAuthService>() with singleton {
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

    bind<RedditCredentials>() with singleton {
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

    bind<PlaylistUpdateService>() with factory { updateDetails: UpdateDetails ->
        val (configuration, playlist) = updateDetails
        val redditService = RedditServiceImpl(
            playlist,
            configuration.flairsToExclude,
            Arguments.redditMaxPage,
            instance()
        )
        val trackFilter = TrackFilter(configuration, playlist)
        PlaylistUpdateService(
            playlist,
            redditService,
            trackFilter,
            instance(),
            instance()
        )
    }
}
