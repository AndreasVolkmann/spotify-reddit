package me.avo.spottit.service

import me.avo.spottit.data.UpdateDetails
import me.avo.spottit.model.Configuration
import me.avo.spottit.service.spotify.SpotifyAuthService
import me.avo.spottit.service.spotify.SpotifyService
import me.avo.spottit.util.Scheduler
import org.slf4j.LoggerFactory

class DynamicPlaylistService(
    private val spotifyAuthService: SpotifyAuthService,
    private val spotifyService: SpotifyService,
    private val getUpdateService: (UpdateDetails) -> TrackFinderService
) {

    fun run(configuration: Configuration) {
        logger.info("Update playlists")
        if (Scheduler.shouldExecute(configuration.schedule)) {
            spotifyAuthService.refresh()
            updatePlaylists(configuration)
        }
    }

    private fun updatePlaylists(configuration: Configuration) = configuration.playlists.forEach { playlist ->
        val updateDetails = UpdateDetails(configuration, playlist)
        val foundTracks = getUpdateService(updateDetails).run()
        spotifyService.updatePlaylist(foundTracks, playlist.id, playlist.maxSize)
    }


    private val logger = LoggerFactory.getLogger(this::class.java)

}
