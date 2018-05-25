package me.avo.spottit.service

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
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

    override fun findTracks(tracks: List<RedditTrack>, searchAlgorithm: SpotifySearchAlgorithm): List<Track> =
        authService.getSpotifyApi().searchForTracks(tracks, searchAlgorithm)

    private fun SpotifyApi.searchForTracks(
        tracks: List<RedditTrack>,
        searchAlgorithm: SpotifySearchAlgorithm
    ): List<Track> = searchAlgorithm.searchForTracks(this, tracks)

    private fun SpotifyApi.clearPlaylist(
        tracksToAdd: List<Track>, userId: String, playlistId: String, maxSize: Int
    ): List<Track> {
        logger.info("Clearing Playlist")
        val tracksInPlaylist = getPlaylistsTracks(userId, playlistId).build().execute().items.map { it.track }
        return when {
            tracksInPlaylist.isEmpty() -> {
                logger.info("The playlist is currently empty, all found tracks will be added")
                tracksToAdd
            }
            else -> {
                val idsToAdd = tracksToAdd.map { it.id }
                val (willBeAddedAgain, willBeRemoved) = tracksInPlaylist.partition { idsToAdd.contains(it.id) }
                val currentSize = tracksInPlaylist.size

                val sizeAfterRemoval = currentSize - willBeRemoved.size
                val tracksToRemove = if (sizeAfterRemoval > maxSize) {
                    willBeAddedAgain.takeLast(sizeAfterRemoval - maxSize)
                } else listOf()

                tracksInPlaylist.filter { it.id !in idsToAdd }

                logger.info("${willBeAddedAgain.size} tracks are already in the playlist, ${tracksToRemove.size} tracks will be removed")
                val jsonTracks = tracksToRemove.map { jsonObject("uri" to it.uri) }.toJsonArray()
                removeTracksFromPlaylist(userId, playlistId, jsonTracks).build().execute()
                calculateTracksToAdd(tracksInPlaylist.size, tracksToAdd, willBeAddedAgain, maxSize)
            }
        }
    }

    fun calculateTracksToAdd(
        amountInPlaylist: Int,
        tracksToAdd: List<Track>,
        willBeAddedAgain: List<Track>,
        maxSize: Int
    ): List<Track> {
        val amountToAdd = maxSize - amountInPlaylist
        return if (amountToAdd < 1) listOf()
        else {
            val againIds = willBeAddedAgain.map { it.id }
            tracksToAdd
                .filterNot { againIds.contains(it.id) }
                .take(amountToAdd)
        }
    }

    private fun SpotifyApi.addTracks(tracks: Collection<Track>, userId: String, playlistId: String) {
        if (tracks.isEmpty()) {
            logger.warn("Did not find any tracks to add")
            return
        }
        addTracksToPlaylist(userId, playlistId, tracks.map { it.uri }.toTypedArray()).build().execute()
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}