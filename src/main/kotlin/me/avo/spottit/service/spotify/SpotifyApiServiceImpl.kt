package me.avo.spottit.service.spotify

import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.BadGatewayException
import com.wrapper.spotify.exceptions.detailed.ServiceUnavailableException
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException
import com.wrapper.spotify.model_objects.specification.Album
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.PlaylistTrack
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.TrackInPlaylist
import me.avo.spottit.util.RetrySupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SpotifyApiServiceImpl(private val spotifyApi: SpotifyApi) : SpotifyApiService, RetrySupport {

    override fun getTrack(id: String): Track? = spotifyApi.getTrack(id).execute()

    override fun getAlbum(id: String): Album = spotifyApi.getAlbum(id).execute()

    override fun getAlbumForTrack(track: Track): Album = getAlbum(track.album.id)

    override fun searchTracks(query: String): Paging<Track> =
        spotifyApi.searchTracks(query).limit(10).offset(0).execute()

    override fun getPlaylistsTracks(playlistId: String): Paging<PlaylistTrack> {
        return spotifyApi.getPlaylistsTracks(playlistId).execute()
    }

    override fun removeTracksFromPlaylist(playlistId: String, tracks: List<TrackInPlaylist>) {
        val jsonTracks = makeJsonTracksForRemoval(tracks)
        spotifyApi.removeTracksFromPlaylist(playlistId, jsonTracks)
    }

    override fun addTracksToPlaylist(playlistId: String, tracks: Collection<Track>) {
        spotifyApi.addTracksToPlaylist(playlistId, tracks.map { it.uri }.toTypedArray())
    }

    private fun makeJsonTracksForRemoval(tracks: List<TrackInPlaylist>) = tracks.map {
        jsonObject("uri" to it.track.uri, "positions" to jsonArray(it.index))
    }.toJsonArray()

    override fun mapTimeout(ex: Exception): Long = when (ex) {
        is TooManyRequestsException -> ex.retryAfter.toLong()
        is ServiceUnavailableException -> 5000
        is BadGatewayException -> 2000
        else -> 1000
    }

    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
}
