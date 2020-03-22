package me.avo.spottit.service

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.service.reddit.RedditService
import me.avo.spottit.service.spotify.SpotifyAuthService
import me.avo.spottit.service.spotify.SpotifyService
import me.avo.spottit.util.Scheduler
import me.avo.spottit.util.TrackFilter
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.slf4j.LoggerFactory

class DynamicPlaylistService(
    private val spotifyAuthService: SpotifyAuthService,
    private val spotifyService: SpotifyService,
    private val getRedditService: (Playlist, List<String>) -> RedditService,
    private val getTrackFilter: (Configuration, Playlist) -> TrackFilter
) {

    fun updatePlaylists(configuration: Configuration) {
        if (!Scheduler.shouldExecute(configuration.schedule)) return
        spotifyAuthService.refresh()
        configuration.playlists.forEach {
            val redditService = getRedditService(it, configuration.flairsToExclude)
            val trackFilter = getTrackFilter(configuration, it)
            processPlaylist(it, redditService, trackFilter)
        }
    }

    private fun processPlaylist(playlist: Playlist, redditService: RedditService, trackFilter: TrackFilter) {
        logger.info("Processing playlist ${playlist.id}")
        val foundTracks = mutableListOf<Track>()

        while (foundTracks.size < playlist.maxSize && !redditService.isDone) redditService
            .getTracks()
            .let { spotifyService.findTracks(it, trackFilter) }
            .filterNot { it.id in foundTracks.map { it.id } }
            .distinctBy { it.id } // TODO find out why there are duplicates
            .also { redditService.update(it.size) }
            .mapTo(foundTracks) { it }
            .also { logger.info("Status: ${foundTracks.size} / ${playlist.maxSize} tracks have been found") }

        playlist.checkFoundAmount(foundTracks.size)
        spotifyService.updatePlaylist(foundTracks, playlist.id, playlist.maxSize)
    }

    private fun Playlist.checkFoundAmount(found: Int) {
        if (sort == SubredditSort.TOP
            && timePeriod == TimePeriod.ALL
            && maxSize / 10 > found
        ) throw IllegalStateException("Top playlist $id ($subreddit) found too few tracks. $found / $maxSize")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
