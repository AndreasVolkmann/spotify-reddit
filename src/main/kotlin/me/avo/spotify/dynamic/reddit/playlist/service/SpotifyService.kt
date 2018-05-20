package me.avo.spotify.dynamic.reddit.playlist.service

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spotify.dynamic.reddit.playlist.model.RedditTrack

interface SpotifyService {

    fun updatePlaylist(tracks: List<Track>, userId: String, playlistId: String)

    fun findTracks(tracks: List<RedditTrack>): List<Track>

}