package me.avo.spottit.service.spotify

import com.wrapper.spotify.model_objects.special.SnapshotResult
import com.wrapper.spotify.model_objects.specification.Album
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.PlaylistTrack
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.TrackInPlaylist

/**
 * Spotify api wrapper.
 */
interface SpotifyApiService {

    fun getTrack(id: String): Track?

    fun getPlaylistsTracks(playlistId: String): Paging<PlaylistTrack>

    fun getAlbum(id: String): Album

    fun getAlbumForTrack(track: Track): Album

    fun searchTracks(query: String): Paging<Track>

    fun removeTracksFromPlaylist(playlistId: String, tracks: List<TrackInPlaylist>): SnapshotResult

    fun addTracksToPlaylist(playlistId: String, tracks: Collection<Track>): SnapshotResult
}
