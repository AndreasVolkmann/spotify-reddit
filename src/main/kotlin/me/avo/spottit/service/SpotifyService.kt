package me.avo.spottit.service

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack

interface SpotifyService {

    fun updatePlaylist(tracks: List<Track>, userId: String, playlistId: String)

    fun findTracks(tracks: List<RedditTrack>): List<Track>

}