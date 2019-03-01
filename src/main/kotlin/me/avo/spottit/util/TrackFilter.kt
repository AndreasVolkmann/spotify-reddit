package me.avo.spottit.util

import com.wrapper.spotify.enums.ReleaseDatePrecision
import com.wrapper.spotify.enums.ReleaseDatePrecision.*
import com.wrapper.spotify.model_objects.specification.Album
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.config.Arguments
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import org.slf4j.LoggerFactory

class TrackFilter(private val configuration: Configuration, playlist: Playlist) {

    fun applyFilters(tracks: Array<Track>): Array<Track> = tracks
        .filter(::checkTrackLength)
        .toTypedArray()

    fun checkTrackLength(track: Track) = track.durationMs / 1000 > configuration.minimumLength

    fun checkTrackAgeByAlbum(album: Album): Boolean = when {
        doCheckReleaseDate -> {
            val pattern = getPattern(album.releaseDatePrecision)
            val date = parseDateString(album.releaseDate, pattern)
            val valid = datesToCheck.none { it.after(date) }
            if (!valid) {
                logger.info("Album ${album.name} excluded because of its date: $date")
            }
            valid
        }
        else -> true
    }

    fun timeout() {
        Thread.sleep(configuration.rateLimitInMs)
    }

    private fun getPattern(precision: ReleaseDatePrecision): String = when (precision) {
        DAY -> "yyyy-MM-dd"
        MONTH -> "yyyy-MM"
        YEAR -> "yyyy"
    }

    val isStrictMix = playlist.isStrictMix
    private val dateFilter = playlist.dateFilter
    private val datesToCheck = listOfNotNull(dateFilter.startingFrom, dateFilter.maxDistance)
    val doCheckReleaseDate = datesToCheck.isNotEmpty()

    val editDistanceThreshold = Arguments.editDistance

    private val logger = LoggerFactory.getLogger(this::class.java)

}
