package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack

interface SpotifySearchAlgorithm {

    fun searchForTracks(spotifyApi: SpotifyApi, tracks: List<RedditTrack>): List<Track>

}