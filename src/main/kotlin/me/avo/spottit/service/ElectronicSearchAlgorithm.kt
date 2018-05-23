package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.SpotifyQueryTools
import org.slf4j.LoggerFactory

class ElectronicSearchAlgorithm : SpotifySearchAlgorithm {

    override fun searchForTracks(spotifyApi: SpotifyApi, tracks: List<RedditTrack>): List<Track> = with(spotifyApi) {
        tracks
            .map { findTrack(it) }
            .mapNotNull { (reddit, chosenTrack, total) ->
                logger.info("Found $total results for: $reddit | ${chosenTrack?.artists?.joinToString { it.name }} - ${chosenTrack?.name}")
                chosenTrack
            }
            .onEach { Thread.sleep(250) }
    }

    private fun SpotifyApi.findTrack(track: RedditTrack): Triple<RedditTrack, Track?, Int> =
        SpotifyQueryTools.initialQuery(track)
            .let { searchQuery(it) }
            .let { results ->
                val chosenTrack = evaluateResults(track, results.items, 0)
                Triple(track, chosenTrack, results.total)
            }


    private fun SpotifyApi.searchQuery(query: String): Paging<Track> {
        logger.debug("Searching for $query")
        return searchTracks(query).limit(10).offset(0).build().execute()
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

    private val logger = LoggerFactory.getLogger(this::class.java)
}