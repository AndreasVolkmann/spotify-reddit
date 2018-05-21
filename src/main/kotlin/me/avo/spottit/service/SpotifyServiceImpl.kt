package me.avo.spottit.service

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.SpotifyQueryTools
import org.slf4j.LoggerFactory

class SpotifyServiceImpl(
    private val authService: SpotifyAuthService
) : SpotifyService {

    override fun updatePlaylist(tracks: List<Track>, userId: String, playlistId: String) = authService
        .also { logger.info("Updating Playlist with ${tracks.size} potential tracks") }
        .getSpotifyApi().run {
            clearPlaylist(tracks, userId, playlistId)
                .also { logger.info("Adding ${it.size} tracks to the playlist") }
                .let { addTracks(it, userId, playlistId) }
        }

    override fun findTracks(tracks: List<RedditTrack>): List<Track> = authService.getSpotifyApi()
        .searchForTracks(tracks)

    private fun SpotifyApi.searchForTracks(tracks: List<RedditTrack>): List<Track> = tracks
        .map { findTrack(it) }
        .mapNotNull { (reddit, chosenTrack, total) ->
            logger.info("Found $total results for: $reddit | ${chosenTrack?.artists?.joinToString { it.name }} - ${chosenTrack?.name}")
            chosenTrack
        }
        .onEach { Thread.sleep(250) }

    private fun SpotifyApi.findTrack(track: RedditTrack): Triple<RedditTrack, Track?, Int> =
        SpotifyQueryTools.initialQuery(track)
            .let { searchQuery(it) }
            .let { results ->
                val chosenTrack = evaluateResults(track, results.items, 0)
                Triple(track, chosenTrack, results.total)
            }

    private fun SpotifyApi.evaluateResults(track: RedditTrack, items: Array<Track>, stack: Int): Track? =
        when (items.size) {
            0 -> adjustQuery(track, stack) // couldn't find anything, adjust query
            1 -> items.first() // only one result
            else -> SpotifyQueryTools.sortItems(items, track).first() // there are multiple results, sort
        }

    private fun SpotifyApi.adjustQuery(track: RedditTrack, stack: Int) = when {
        stack > 1 -> null
        track.artist.contains("&") -> {
            val fixedArtist = track.artist.split("&").first().trim()
            research(track.copy(artist = fixedArtist), stack, fixedArtist, track.title, track.mix)
        }
        track.mix != null -> research(track, stack, track.artist, track.title)
        else -> null
    }

    private fun SpotifyApi.research(track: RedditTrack, stack: Int, vararg items: String?) =
        searchQuery(SpotifyQueryTools.makeQuery(*items)).let {
            evaluateResults(track, it.items, stack + 1)
        }

    private fun SpotifyApi.searchQuery(query: String): Paging<Track> {
        logger.debug("Searching for $query")
        return searchTracks(query).limit(10).offset(0).build().execute()
    }

    private fun SpotifyApi.clearPlaylist(tracksToAdd: List<Track>, userId: String, playlistId: String): List<Track> {
        logger.info("Clearing Playlist")
        val idsToAdd = tracksToAdd.map { it.id }
        val tracksInPlaylist = getPlaylistsTracks(userId, playlistId).build().execute().items.map { it.track }

        return when {
            tracksInPlaylist.isEmpty() -> {
                logger.info("The playlist is currently empty, all found tracks will be added")
                tracksToAdd
            }
            else -> {
                val (willBeAddedAgain, willBeRemoved) = tracksInPlaylist.partition { idsToAdd.contains(it.id) }
                logger.info("${willBeAddedAgain.size} tracks are already in the playlist, ${willBeRemoved.size} tracks will be removed")
                val jsonTracks = willBeRemoved.map { jsonObject("uri" to it.uri) }.toJsonArray()
                removeTracksFromPlaylist(userId, playlistId, jsonTracks).build().execute()
                val againIds = willBeAddedAgain.map { it.id }
                tracksToAdd.filterNot { againIds.contains(it.id) }
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