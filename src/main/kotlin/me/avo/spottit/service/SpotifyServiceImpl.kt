package me.avo.spottit.service

import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.model.TrackInPlaylist
import me.avo.spottit.util.SpotifyPlaylistCalculator
import org.slf4j.LoggerFactory

class SpotifyServiceImpl(
    private val authService: SpotifyAuthService
) : SpotifyService {

    override fun updatePlaylist(tracks: List<Track>, userId: String, playlistId: String, maxSize: Int) = authService
        .also { logger.info("Updating Playlist with ${tracks.size} potential tracks") }
        .getSpotifyApi().run {
            clearPlaylist(tracks, userId, playlistId, maxSize)
                .also { logger.info("Adding ${it.size} tracks to the playlist") }
                .let { addTracks(it, userId, playlistId) }
        }

    override fun findTracks(tracks: List<RedditTrack>, searchAlgorithm: SpotifySearchAlgorithm): List<Track> {
        val api = authService.getSpotifyApi()
        return searchAlgorithm.searchForTracks(api, tracks)
    }

    private fun SpotifyApi.clearPlaylist(
        tracksToAdd: List<Track>, userId: String, playlistId: String, maxSize: Int
    ): List<Track> {
        logger.info("Clearing Playlist")
        val tracksInPlaylist = getPlaylistsTracks(userId, playlistId).build().execute().items.map { it.track }
        return when {
            tracksInPlaylist.isEmpty() -> SpotifyPlaylistCalculator.addMaxSizeTracks(tracksToAdd, maxSize)
            else -> {
                val (remove, add) = SpotifyPlaylistCalculator.calculateTracksToRemoveAndAdd(
                    tracksToAdd,
                    maxSize,
                    tracksInPlaylist
                )
                removeTracksFromPlaylist(userId, playlistId, remove)
                add
            }
        }
    }

    private fun SpotifyApi.removeTracksFromPlaylist(userId: String, playlistId: String, tracks: List<TrackInPlaylist>) {
        val jsonTracks = makeJsonTracksForRemoval(tracks)
        removeTracksFromPlaylist(userId, playlistId, jsonTracks).build().execute()
    }

    private fun makeJsonTracksForRemoval(tracks: List<TrackInPlaylist>) = tracks.map {
        jsonObject("uri" to it.track.uri, "positions" to jsonArray(it.index))
    }.toJsonArray()

    private fun SpotifyApi.addTracks(tracks: Collection<Track>, userId: String, playlistId: String) {
        if (tracks.isEmpty()) {
            logger.warn("Did not find any tracks to add")
            return
        }
        addTracksToPlaylist(userId, playlistId, tracks.map { it.uri }.toTypedArray()).build().execute()
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}