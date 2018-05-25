package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.Configuration

class TrackFilter(private val configuration: Configuration) {

    fun applyFilters(tracks: Array<Track>): Array<Track> = tracks.filter(::checkTrackLength).toTypedArray()

    fun checkTrackLength(track: Track) =
        track.durationMs / 1000 > configuration.minimumLength

}