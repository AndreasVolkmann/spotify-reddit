package me.avo.spottit.service.spotify

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.data.TrackResult
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.SpotifyQueryTools
import me.avo.spottit.util.TrackFilter
import org.slf4j.LoggerFactory

class ElectronicSearchAlgorithm(
    private val spotifyApi: SpotifyApiService,
    private val trackFilter: TrackFilter
) : SpotifySearchAlgorithm {

    override fun searchForTracks(tracks: List<RedditTrack>): List<Track> = tracks
        .asSequence()
        .map(::tryFindTrack)
        .mapNotNull { (reddit, chosenTrack, total) ->
            logger.info("Found $total results for: $reddit | ${chosenTrack?.artists?.joinToString { it.name }} - ${chosenTrack?.name}")
            chosenTrack
        }
        .onEach { trackFilter.timeout() }
        .toList()

    private fun tryFindTrack(track: RedditTrack): TrackResult = try {
        findTrack(track)
    } catch (ex: Exception) {
        logger.warn("Exception finding track $track", ex)
        TrackResult(track, null, 0)
    }

    private fun findTrack(track: RedditTrack): TrackResult = when {
        track.isSpotifyTrack -> getTrack(track)
        else -> searchForTrack(track)
    }

    private fun getTrack(track: RedditTrack): TrackResult {
        val id = track.url!!.toString().substringAfter("/track/").substringBefore("?")
        val spotifyTrack = spotifyApi.getTrack(id)
        return TrackResult(track, spotifyTrack, if (spotifyTrack != null) 1 else 0)
    }

    private fun searchForTrack(track: RedditTrack): TrackResult =
        SpotifyQueryTools.initialQuery(track, useTags = true)
            .let(::searchQuery)
            .let { (total, items) ->
                val chosenTrack = evaluateResults(track, items, 0)?.let {
                    if (trackFilter.doCheckReleaseDate) {
                        val album = spotifyApi.getAlbumForTrack(it)
                        if (trackFilter.checkTrackAgeByAlbum(album)) it else null
                    } else it
                }
                TrackResult(track, chosenTrack, total)
            }

    private fun searchQuery(query: String): Pair<Int, Array<Track>> {
        logger.debug("Searching for $query")
        val paging = spotifyApi.searchTracks(query)
        Thread.sleep(100)
        return paging.total to trackFilter.applyFilters(paging.items)
    }

    fun evaluateResults(track: RedditTrack, items: Array<Track>, stack: Int): Track? = when (items.size) {
        0 -> adjustQuery(track, stack) // couldn't find anything, adjust query
        1 -> items.first() // only one result
        else -> SpotifyQueryTools.sortItems(items, track, trackFilter.editDistanceThreshold).firstOrNull()
                ?: evaluateResults(track, emptyArray(), stack + 1) // there are multiple results, sort
    }?.let {
        // Hotfix, needs refactoring
        SpotifyQueryTools.sortItems(arrayOf(it), track, trackFilter.editDistanceThreshold).firstOrNull()
    }

    private fun adjustQuery(track: RedditTrack, stack: Int) = when {
        stack > 1 -> null
        track.artist.contains("&") -> alteredArtistSearch("&", track, stack)
        track.mix != null && !trackFilter.isStrictMix -> research(track, stack, track.artist, track.title)
        stack == 0 -> research(track, stack, track.artist, track.title, track.mix)
        else -> null
    }

    private fun alteredArtistSearch(delimiter: String, track: RedditTrack, stack: Int): Track? {
        val fixedArtist = track.artist.split(delimiter).first().trim()
        return research(track.copy(artist = fixedArtist), stack, fixedArtist, track.title, track.mix)
    }

    private fun research(track: RedditTrack, stack: Int, vararg items: String?) =
        searchQuery(SpotifyQueryTools.makeQuery(*items)).let { (_, items) ->
            evaluateResults(track, items, stack + 1)
        }

    private val logger = LoggerFactory.getLogger(this::class.java)
}
