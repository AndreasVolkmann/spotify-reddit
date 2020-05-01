package me.avo.spottit.service

import me.avo.spottit.data.UpdateDetails
import me.avo.spottit.model.Configuration
import me.avo.spottit.service.spotify.PlaylistUpdateService
import me.avo.spottit.service.spotify.SpotifyAuthService
import me.avo.spottit.util.Scheduler
import org.slf4j.LoggerFactory

class DynamicPlaylistService(
    private val spotifyAuthService: SpotifyAuthService,
    private val getUpdateService: (UpdateDetails) -> PlaylistUpdateService
) {

    fun updatePlaylists(configuration: Configuration) {
        logger.info("Update playlists")
        if (!Scheduler.shouldExecute(configuration.schedule)) return
        spotifyAuthService.refresh()
        configuration.playlists
            .map { UpdateDetails(configuration, it) }
            .map(getUpdateService)
            .forEach(PlaylistUpdateService::run)
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
