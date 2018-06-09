package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Album
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist

class TrackFilter(private val configuration: Configuration, playlist: Playlist) {

    fun applyFilters(tracks: Array<Track>): Array<Track> = tracks
        .filter(::checkTrackLength)
        .toTypedArray()

    fun checkTrackLength(track: Track) =
        track.durationMs / 1000 > configuration.minimumLength

    fun checkTrackAgeByAlbum(album: Album) {
        album.releaseDate
        album.releaseDatePrecision
    }

    fun timeout() {
        Thread.sleep(configuration.rateLimitInMs)
    }

    val isStrictMix = playlist.isStrictMix

}