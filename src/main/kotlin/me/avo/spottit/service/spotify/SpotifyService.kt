package me.avo.spottit.service.spotify

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.TrackFilter

interface SpotifyService {

    fun updatePlaylist(tracks: List<Track>, playlistId: String, maxSize: Int)

    fun findTracks(tracks: List<RedditTrack>, trackFilter: TrackFilter): List<Track>

}
