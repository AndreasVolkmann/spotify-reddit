package me.avo.spottit.service

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.exception.TooFewTopAllTracksException
import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.service.reddit.RedditService
import me.avo.spottit.service.spotify.SpotifyService
import me.avo.spottit.util.TrackFilter
import me.avo.spottit.util.format
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.slf4j.LoggerFactory

class TrackFinderService(
    private val playlist: Playlist,
    private val redditService: RedditService,
    private val trackFilter: TrackFilter,
    private val spotifyService: SpotifyService
) {
    private val foundTracks = mutableListOf<Track>()
    private val trackIds = mutableSetOf<String>()
    private val trackNames = mutableSetOf<String>()

    fun run(): List<Track> {
        logger.info("Processing $playlist")

        while (shouldContinue()) {
            val tracks = findTracks()
            addTracks(tracks)
            redditService.setCurrentSize(foundTracks.size)
            logger.info("Status: ${foundTracks.size} / ${playlist.maxSize} tracks have been found")
        }

        playlist.checkFoundAmount(foundTracks.size)
        return foundTracks
    }

    private fun shouldContinue() = foundTracks.size < playlist.maxSize && !redditService.isDone()

    private fun findTracks() = redditService
        .getRedditTracks()
        .let(::findSpotifyTracks)

    private fun findSpotifyTracks(redditTracks: List<RedditTrack>): List<Track> =
        spotifyService.findTracks(redditTracks, trackFilter)

    /**
     * Filter out already found tracks by id and formatted name.
     */
    private fun addTracks(tracks: List<Track>) = tracks.forEach {
        if (!trackIds.contains(it.id) && !trackNames.contains(it.format())) {
            addTrack(it)
        }
    }

    private fun addTrack(track: Track) {
        foundTracks.add(track)
        trackIds.add(track.id)
        trackNames.add(track.format())
    }

    /**
     * Verify that top/all playlists have a minimum amount of tracks.
     */
    private fun Playlist.checkFoundAmount(found: Int) {
        if (sort == SubredditSort.TOP
            && timePeriod == TimePeriod.ALL
            && maxSize / 10 > found
        ) throw TooFewTopAllTracksException(this, found)
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
}
