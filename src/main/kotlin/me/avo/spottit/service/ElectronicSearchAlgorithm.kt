package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.SpotifyQueryTools
import me.avo.spottit.util.TrackFilter
import org.slf4j.LoggerFactory

class ElectronicSearchAlgorithm(private val trackFilter: TrackFilter) : SpotifySearchAlgorithm {

    override fun searchForTracks(spotifyApi: SpotifyApi, tracks: List<RedditTrack>): List<Track> = with(spotifyApi) {
        tracks
            .asSequence()
            .map { findTrack(it) }
            .mapNotNull { (reddit, chosenTrack, total) ->
                logger.info("Found $total results for: $reddit | ${chosenTrack?.artists?.joinToString { it.name }} - ${chosenTrack?.name}")
                chosenTrack
            }
            .onEach { trackFilter.timeout() }
            .toList()
    }

    private fun SpotifyApi.findTrack(
        track: RedditTrack
    ): Triple<RedditTrack, Track?, Int> = SpotifyQueryTools.initialQuery(track)
        .let { searchQuery(it) }
        .let { (total, items) ->
            val validTracks = trackFilter.applyFilters(items)
            val chosenTrack = evaluateResults(track, validTracks, 0)
            Triple(track, chosenTrack, total)
        }

    private fun SpotifyApi.searchQuery(query: String): Pair<Int, Array<Track>> = query
        .also { logger.debug("Searching for $it") }
        .let { searchTracks(it) }.limit(10).offset(0).build().executeRequest()
        .also { Thread.sleep(100) }
        .let { it.total to trackFilter.applyFilters(it.items) }

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
        searchQuery(SpotifyQueryTools.makeQuery(*items)).let { (_, items) ->
            evaluateResults(track, items, stack + 1)
        }

    private val logger = LoggerFactory.getLogger(this::class.java)
}