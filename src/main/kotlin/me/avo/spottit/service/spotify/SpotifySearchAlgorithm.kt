package me.avo.spottit.service.spotify

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack

interface SpotifySearchAlgorithm {

    fun searchForTracks(tracks: List<RedditTrack>): List<Track>
}
