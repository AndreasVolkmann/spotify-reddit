package me.avo.spottit.service.spotify

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.Playlist
import me.avo.spottit.service.reddit.RedditService
import me.avo.spottit.util.TrackFilter
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.slf4j.LoggerFactory

class PlaylistUpdateService(
    private val playlist: Playlist,
    private val redditService: RedditService,
    private val trackFilter: TrackFilter,
    private val spotifyService: SpotifyService,
    private val spotifyApi: SpotifyApiService
) {
    private val foundTracks = mutableListOf<Track>()
    private val tracksInPlaylist by lazy { spotifyApi.getPlaylistsTracks(playlist.id) }

    fun run() {
        logger.info("Processing playlist ${playlist.id}")

        while (foundTracks.size < playlist.maxSize && !redditService.isDone) redditService
            .getTracks()
            .let { spotifyService.findTracks(it, trackFilter) }
            .filter { it.filter() }
            .distinctBy { it.id } // TODO find out why there are duplicates
            .also { redditService.update(it.size) }
            .mapTo(foundTracks) { it }
            .also { logger.info("Status: ${foundTracks.size} / ${playlist.maxSize} tracks have been found") }

        playlist.checkFoundAmount(foundTracks.size)
        spotifyService.updatePlaylist(foundTracks, playlist.id, playlist.maxSize)
    }

    private fun Track.filter(): Boolean {
        return id !in foundTracks.map { it.id }
    }

    private fun Playlist.checkFoundAmount(found: Int) {
        if (sort == SubredditSort.TOP
            && timePeriod == TimePeriod.ALL
            && maxSize / 10 > found
        ) throw IllegalStateException("Top playlist $id ($subreddit) found too few tracks. $found / $maxSize")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
