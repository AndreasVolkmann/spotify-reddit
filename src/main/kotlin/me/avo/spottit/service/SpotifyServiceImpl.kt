package me.avo.spottit.service

import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.model.TrackInPlaylist
import me.avo.spottit.util.SpotifyPlaylistCalculator
import me.avo.spottit.util.TrackFilter
import me.avo.spottit.util.format
import org.slf4j.LoggerFactory

class SpotifyServiceImpl(
    private val authService: SpotifyAuthService,
    private val getSearchAlgorithm: (TrackFilter) -> SpotifySearchAlgorithm
) : SpotifyService {

    override fun updatePlaylist(tracks: List<Track>, playlistId: String, maxSize: Int) {
        logger.info("Updating Playlist with ${tracks.size} potential tracks")
        val api = authService.getSpotifyApi()
        val tracksToAdd = api.clearPlaylist(tracks, playlistId, maxSize)
        logger.info("Adding ${tracksToAdd.size} tracks to the playlist")
        api.addTracks(tracksToAdd, playlistId)
    }

    override fun findTracks(tracks: List<RedditTrack>, trackFilter: TrackFilter): List<Track> =
        getSearchAlgorithm(trackFilter).searchForTracks(tracks)

    private fun SpotifyApi.clearPlaylist(tracksToAdd: List<Track>, playlistId: String, maxSize: Int): List<Track> {
        logger.info("Clearing Playlist")
        val tracksInPlaylist = getPlaylistsTracks(playlistId).build().execute().items.map { it.track }
        return when {
            tracksInPlaylist.isEmpty() -> SpotifyPlaylistCalculator.addMaxSizeTracks(tracksToAdd, maxSize)
            else -> {
                val (toRemove, toAdd) = SpotifyPlaylistCalculator.calculateTracksToRemoveAndAdd(
                    tracksToAdd, maxSize, tracksInPlaylist
                )
                removeTracksFromPlaylist(playlistId, toRemove)
                toAdd
            }
        }
    }

    private fun SpotifyApi.removeTracksFromPlaylist(playlistId: String, tracks: List<TrackInPlaylist>) {
        tracks.forEach { logger.info("Removing ${it.track.format()}") }
        val jsonTracks = makeJsonTracksForRemoval(tracks)
        removeTracksFromPlaylist(playlistId, jsonTracks).build().execute()
    }

    private fun makeJsonTracksForRemoval(tracks: List<TrackInPlaylist>) = tracks.map {
        jsonObject("uri" to it.track.uri, "positions" to jsonArray(it.index))
    }.toJsonArray()

    private fun SpotifyApi.addTracks(tracks: Collection<Track>, playlistId: String) {
        if (tracks.isEmpty()) {
            logger.warn("Did not find any tracks to add")
            return
        }
        tracks.forEach { logger.info("Adding ${it.format()}") }
        addTracksToPlaylist(playlistId, tracks.map { it.uri }.toTypedArray()).build().execute()
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
