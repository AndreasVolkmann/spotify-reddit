package me.avo.spottit.service

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.util.Scheduler
import me.avo.spottit.util.TrackFilter
import org.slf4j.LoggerFactory

class DynamicPlaylistService(
    private val refreshService: TokenRefreshService,
    private val spotifyService: SpotifyService,
    private val redditCredentials: RedditCredentials
) {

    fun updatePlaylists(configuration: Configuration) = when {
        Scheduler.shouldExecute(configuration.schedule) -> {
            refreshService.refresh()
            configuration.playlists.forEach {
                processPlaylist(configuration, it)
            }
        }
        else -> Unit
    }

    private fun processPlaylist(configuration: Configuration, playlist: Playlist) {
        logger.info("Processing playlist ${playlist.id}")
        val redditService = RedditServiceImpl(playlist, configuration.flairsToExclude, redditCredentials)
        val foundTracks = mutableListOf<Track>()
        val trackFilter = TrackFilter(configuration, playlist)

        while (foundTracks.size < playlist.maxSize && !redditService.isDone) redditService
            .getTracks()
            .let { spotifyService.findTracks(it, trackFilter) }
            .filterNot { it.id in foundTracks.map { it.id } }
            .distinctBy { it.id } // TODO find out why there are duplicates
            .also { redditService.update(it.size) }
            .mapTo(foundTracks) { it }
            .also { logger.info("Status: ${foundTracks.size} / ${playlist.maxSize} tracks have been found") }

        spotifyService.updatePlaylist(foundTracks, playlist.id, playlist.maxSize)
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
