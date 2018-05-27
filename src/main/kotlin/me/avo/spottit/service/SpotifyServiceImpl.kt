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
            tracksInPlaylist.isEmpty() -> addMaxSizeTracks(tracksToAdd, maxSize)
            else -> {
                val (remove, add) = calculateTracksToRemoveAndAdd(tracksToAdd, maxSize, tracksInPlaylist)
                removeTracksFromPlaylist(userId, playlistId, this, remove)
                add
            }
        }
    }

    fun removeTracksFromPlaylist(userId: String, playlistId: String, api: SpotifyApi, tracks: List<TrackInPlaylist>) {
        val jsonTracks = makeJsonTracksForRemoval(tracks)
        api.removeTracksFromPlaylist(userId, playlistId, jsonTracks).build().execute()
    }

    fun makeJsonTracksForRemoval(tracks: List<TrackInPlaylist>) = tracks.map {
        jsonObject("uri" to it.track.uri, "positions" to jsonArray(it.index))
    }.toJsonArray()

    fun calculateTracksToRemoveAndAdd(
        tracksToAdd: List<Track>, maxSize: Int, tracksInPlaylist: List<Track>
    ): Pair<List<TrackInPlaylist>, List<Track>> {
        val idsToAdd = tracksToAdd.map { it.id }
        val (tracksToRemove, willBeAddedAgain) = calculateToRemoveAndAddAgain(
            tracksInPlaylist,
            idsToAdd,
            maxSize
        )

        logger.info("${tracksInPlaylist.size} tracks are already in the playlist, ${tracksToRemove.size} tracks will be removed, ${willBeAddedAgain.size} tracks will be added again / kept")
        return tracksToRemove to calculateTracksToAdd(
            tracksInPlaylist.size,
            tracksToRemove.size,
            tracksToAdd,
            willBeAddedAgain,
            maxSize
        )
    }

    fun calculateToRemoveAndAddAgain(
        tracksInPlaylist: List<Track>,
        idsToAdd: List<String>,
        maxSize: Int
    ): Pair<List<TrackInPlaylist>, List<Track>> {
        val (willBeAddedAgain, willBeRemoved) = tracksInPlaylist.partition { idsToAdd.contains(it.id) }

        val duplicatesToRemove = willBeAddedAgain
            .groupBy { it.id }
            .filterValues { it.size > 1 }
            .values
            .flatMap { it.drop(1) }

        val willBeRemovedWithDuplicates = willBeRemoved + duplicatesToRemove

        val (remove, addAgain) = calculateTracksToRemove(
            tracksInPlaylist.size,
            willBeAddedAgain,
            willBeRemovedWithDuplicates,
            maxSize
        )

        val indexLookup = SpotifyPlaylistCalculator.createIndexLookup(tracksInPlaylist)
        val tracksInPlaylistToRemove = remove.map { trackToRemove ->
            TrackInPlaylist(
                track = trackToRemove,
                index = indexLookup[trackToRemove.id]!!.pop()
            )
        }

        return tracksInPlaylistToRemove to addAgain
    }

    fun calculateTracksToRemove(
        currentSize: Int, willBeAddedAgain: List<Track>, willBeRemoved: List<Track>, maxSize: Int
    ): Pair<List<Track>, List<Track>> {
        val sizeAfterRemoval = currentSize - willBeRemoved.size
        val (remove, addAgain) = when {
            sizeAfterRemoval > maxSize -> {
                val additionallyRemove = willBeAddedAgain.takeLast(sizeAfterRemoval - maxSize)
                val willBeAddedAgainFiltered = willBeAddedAgain.take(willBeAddedAgain.size - additionallyRemove.size)
                (willBeRemoved + additionallyRemove) to willBeAddedAgainFiltered
            }
            else -> willBeRemoved to willBeAddedAgain
        }
        return remove to addAgain.distinctBy { it.id }
    }

    fun addMaxSizeTracks(tracksToAdd: List<Track>, maxSize: Int): List<Track> {
        logger.info("The playlist is currently empty, all found tracks will be added")
        return tracksToAdd.take(maxSize)
    }

    fun calculateTracksToAdd(
        amountInPlaylist: Int,
        amountToRemove: Int,
        tracksToAdd: List<Track>,
        willBeAddedAgain: List<Track>,
        maxSize: Int
    ): List<Track> {
        val amountToAdd = maxSize - (amountInPlaylist - amountToRemove) // 2
        return when {
            amountToAdd < 1 -> listOf()
            else -> {
                val againIds = willBeAddedAgain.map { it.id }
                tracksToAdd
                    .filterNot { againIds.contains(it.id) }
                    .take(amountToAdd)
            }
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