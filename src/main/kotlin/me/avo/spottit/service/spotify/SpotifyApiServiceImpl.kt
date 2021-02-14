package me.avo.spottit.service.spotify

import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.*
import com.wrapper.spotify.model_objects.special.SnapshotResult
import com.wrapper.spotify.model_objects.specification.Album
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.TrackInPlaylist
import me.avo.spottit.util.RetrySupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SpotifyApiServiceImpl(private val spotifyApi: SpotifyApi) : SpotifyApiService, RetrySupport {

    override fun getTrack(id: String): Track? = try {
        spotifyApi.getTrack(id).execute()
    } catch (ex: NotFoundException) {
        null
    } catch (ex: BadRequestException) {
        null
    }

    override fun getAlbum(id: String): Album = spotifyApi.getAlbum(id).execute()

    override fun getAlbumForTrack(track: Track): Album = getAlbum(track.album.id)

    override fun searchTracks(query: String): Paging<Track> =
        spotifyApi.searchTracks(query).limit(10).offset(0).execute()

    override fun getPlaylistsTracks(playlistId: String): List<Track> {
        return spotifyApi
            .getPlaylistsTracks(playlistId)
            .execute()
            .items.map { it.track }
    }

    override fun removeTracksFromPlaylist(playlistId: String, tracks: List<TrackInPlaylist>): SnapshotResult {
        val jsonTracks = makeJsonTracksForRemoval(tracks)
        return spotifyApi.removeTracksFromPlaylist(playlistId, jsonTracks).execute()
    }

    override fun addTracksToPlaylist(playlistId: String, tracks: Collection<Track>): SnapshotResult {
        return spotifyApi.addTracksToPlaylist(playlistId, tracks.map { it.uri }.toTypedArray()).execute()
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
